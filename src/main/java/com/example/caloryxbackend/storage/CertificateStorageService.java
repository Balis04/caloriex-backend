package com.example.caloryxbackend.storage;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.config.R2StorageProperties;
import com.example.caloryxbackend.coachprofile.payload.CoachCertificateUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final S3Client s3Client;
    private final R2StorageProperties properties;
    private final CurrentUserService currentUserService;

    public CoachCertificateUploadResponse uploadCertificate(MultipartFile file) {
        return uploadDocument(file, "Certificate", "certificate", "coach-certificates", "certificate");
    }

    public CoachCertificateUploadResponse uploadTrainingPlan(MultipartFile file) {
        return uploadDocument(file, "Training plan", "training plan", "training-plans", "training-plan");
    }

    private CoachCertificateUploadResponse uploadDocument(
            MultipartFile file,
            String documentLabel,
            String fileTypeLabel,
            String folderName,
            String fallbackFileName
    ) {
        validateStorageEnabled(documentLabel);
        validateFile(file, documentLabel, fileTypeLabel);

        String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String extension = getExtension(originalFileName, documentLabel);
        String contentType = normalizeContentType(file.getContentType(), extension, fileTypeLabel);
        String objectKey = buildObjectKey(folderName, sanitizeFileName(originalFileName, fallbackFileName), extension);

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.bucket())
                            .key(objectKey)
                            .contentType(contentType)
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException exception) {
            throw new BadRequestException("Failed to read uploaded " + fileTypeLabel);
        } catch (RuntimeException exception) {
            throw new BadRequestException("Failed to upload " + fileTypeLabel + " to storage");
        }

        return new CoachCertificateUploadResponse(
                originalFileName,
                buildFileUrl(objectKey),
                contentType,
                file.getSize()
        );
    }

    private void validateStorageEnabled(String documentLabel) {
        if (!properties.enabled()) {
            throw new BadRequestException(documentLabel + " storage is not configured");
        }
    }

    private void validateFile(MultipartFile file, String documentLabel, String fileTypeLabel) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(documentLabel + " file is required");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new BadRequestException(documentLabel + " file name is required");
        }

        String extension = getExtension(originalFileName, documentLabel);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Only PDF and DOCX " + fileTypeLabel + " files are allowed");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            String normalized = contentType.toLowerCase(Locale.ROOT);
            if (!"application/octet-stream".equals(normalized) && !ALLOWED_CONTENT_TYPES.contains(normalized)) {
                throw new BadRequestException("Unsupported " + fileTypeLabel + " content type");
            }
        }
    }

    private String buildObjectKey(String folderName, String sanitizedFileName, String extension) {
        String auth0Id = currentUserService.getAuth0Id();
        String timestamp = Instant.now().toString().replace(":", "-");
        return "%s/%s/%s-%s-%s.%s".formatted(
                folderName,
                sanitizePathSegment(auth0Id),
                timestamp,
                sanitizedFileName,
                UUID.randomUUID(),
                extension
        );
    }

    private String buildFileUrl(String objectKey) {
        String publicBaseUrl = properties.publicBaseUrl();
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            return UriComponentsBuilder.fromUriString(trimTrailingSlash(publicBaseUrl))
                    .pathSegment(objectKey.split("/"))
                    .toUriString();
        }

        return "%s/%s/%s".formatted(
                trimTrailingSlash(properties.endpoint()),
                properties.bucket(),
                objectKey
        );
    }

    private String sanitizeFileName(String fileName, String fallbackFileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String withoutExtension = normalized.replaceFirst("\\.[^.]+$", "");
        String sanitized = withoutExtension
                .replaceAll("[^A-Za-z0-9._-]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        return sanitized.isBlank() ? fallbackFileName : sanitized;
    }

    private String sanitizePathSegment(String value) {
        return value.replaceAll("[^A-Za-z0-9_-]", "-");
    }

    private String getExtension(String fileName, String documentLabel) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == fileName.length() - 1) {
            throw new BadRequestException(documentLabel + " file extension is required");
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizeContentType(String contentType, String extension, String fileTypeLabel) {
        if (contentType != null && !contentType.isBlank() && !"application/octet-stream".equalsIgnoreCase(contentType)) {
            return contentType.toLowerCase(Locale.ROOT);
        }

        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> throw new BadRequestException("Unsupported " + fileTypeLabel + " file extension");
        };
    }

    private String trimTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
