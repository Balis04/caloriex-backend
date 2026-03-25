package com.example.caloriexbackend.storage;

import com.example.caloriexbackend.common.exception.BadRequestException;
import com.example.caloriexbackend.config.R2StorageAccessMode;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.config.R2StorageProperties;
import com.example.caloriexbackend.storage.payload.ProtectedDocumentUploadResponse;
import com.example.caloriexbackend.storage.payload.PublicDocumentUploadResponse;
import com.example.caloriexbackend.storage.payload.StoredFileDownload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class StorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String DOCX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final S3Client s3Client;
    private final R2StorageProperties properties;
    private final AuthenticatedUserService authenticatedUserService;

    public PublicDocumentUploadResponse uploadCertificate(MultipartFile file) {
        UploadResult upload = uploadDocument(file, "Certificate", "certificate", "coach-certificates", "certificate");

        return new PublicDocumentUploadResponse(
                upload.originalFileName(),
                buildFileUrl(upload.objectKey()),
                upload.contentType(),
                upload.size()
        );
    }

    public ProtectedDocumentUploadResponse uploadTrainingPlan(MultipartFile file) {
        UploadResult upload = uploadDocument(file, "Training plan", "training plan", "training-plans", "training-plan");

        return new ProtectedDocumentUploadResponse(
                upload.originalFileName(),
                upload.objectKey(),
                upload.contentType(),
                upload.size()
        );
    }

    public StoredFileDownload downloadTrainingPlan(String storedKey, String fileName, String contentType) {
        validateStorageEnabled("Training plan");

        String objectKey = resolveObjectKey(storedKey);

        try {
            byte[] content = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(properties.bucket())
                            .key(objectKey)
                            .build()
            ).asByteArray();

            return new StoredFileDownload(
                    fileName,
                    contentType != null && !contentType.isBlank() ? contentType : PDF_CONTENT_TYPE,
                    content
            );
        } catch (RuntimeException exception) {
            throw new BadRequestException("Failed to download training plan from storage");
        }
    }

    private UploadResult uploadDocument(
            MultipartFile file,
            String documentLabel,
            String fileTypeLabel,
            String folderName,
            String fallbackFileName
    ) {
        validateStorageEnabled(documentLabel);
        validateFile(file, documentLabel, fileTypeLabel);

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = fallbackFileName;
        } else {
            originalFileName = originalFileName.replace("\\", "/");

            int lastSlash = originalFileName.lastIndexOf("/");
            if (lastSlash >= 0) {
                originalFileName = originalFileName.substring(lastSlash + 1);
            }

            originalFileName = originalFileName.replace("\0", "");
        }

        String extension = getExtension(originalFileName, documentLabel);
        String contentType = normalizeContentType(file.getContentType(), extension, fileTypeLabel);

        String objectKey = buildObjectKey(
                folderName,
                sanitizeFileName(originalFileName, fallbackFileName),
                extension
        );

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

        return new UploadResult(
                originalFileName,
                objectKey,
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

        String actualFileType = detectActualFileType(file, fileTypeLabel);
        if (!extension.equals(actualFileType)) {
            throw new BadRequestException("Uploaded " + fileTypeLabel + " content does not match the file extension");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            String normalized = contentType.toLowerCase(Locale.ROOT);
            if (!"application/octet-stream".equals(normalized) && !ALLOWED_CONTENT_TYPES.contains(normalized)) {
                throw new BadRequestException("Unsupported " + fileTypeLabel + " content type");
            }

            String expectedContentType = switch (actualFileType) {
                case "pdf" -> PDF_CONTENT_TYPE;
                case "docx" -> DOCX_CONTENT_TYPE;
                default -> throw new BadRequestException("Unsupported " + fileTypeLabel + " file extension");
            };

            if (!"application/octet-stream".equals(normalized) && !expectedContentType.equals(normalized)) {
                throw new BadRequestException("Uploaded " + fileTypeLabel + " content does not match the content type");
            }
        }
    }

    private String buildObjectKey(String folderName, String sanitizedFileName, String extension) {
        String auth0Id = authenticatedUserService.getAuth0Id();
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
        R2StorageAccessMode accessMode = properties.accessMode() != null
                ? properties.accessMode()
                : R2StorageAccessMode.PUBLIC;

        if (accessMode == R2StorageAccessMode.PROTECTED) {
            throw new BadRequestException("Protected file storage mode requires a dedicated protected download flow");
        }

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

    private String resolveObjectKey(String storedKey) {
        if (storedKey == null || storedKey.isBlank()) {
            throw new BadRequestException("Stored training plan key is missing");
        }

        if (!storedKey.startsWith("http://") && !storedKey.startsWith("https://")) {
            return storedKey;
        }

        String normalized = storedKey;

        String publicBaseUrl = properties.publicBaseUrl();
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String prefix = trimTrailingSlash(publicBaseUrl) + "/";
            if (normalized.startsWith(prefix)) {
                return normalized.substring(prefix.length());
            }
        }

        String endpoint = properties.endpoint();
        if (endpoint != null && !endpoint.isBlank()) {
            String prefix = trimTrailingSlash(endpoint) + "/" + properties.bucket() + "/";
            if (normalized.startsWith(prefix)) {
                return normalized.substring(prefix.length());
            }
        }

        throw new BadRequestException("Stored training plan key is not a valid storage object key");
    }

    private String detectActualFileType(MultipartFile file, String fileTypeLabel) {
        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            inputStream.mark(8);
            byte[] header = inputStream.readNBytes(4);
            inputStream.reset();

            if (header.length >= 4
                    && header[0] == '%'
                    && header[1] == 'P'
                    && header[2] == 'D'
                    && header[3] == 'F') {
                return "pdf";
            }

            if (isDocx(inputStream)) {
                return "docx";
            }
        } catch (IOException exception) {
            throw new BadRequestException("Failed to inspect uploaded " + fileTypeLabel);
        }

        throw new BadRequestException("Only real PDF and DOCX " + fileTypeLabel + " files are allowed");
    }

    private boolean isDocx(InputStream inputStream) throws IOException {
        boolean hasContentTypes = false;
        boolean hasWordDocument = false;

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if ("[Content_Types].xml".equals(entryName)) {
                    hasContentTypes = true;
                } else if ("word/document.xml".equals(entryName)) {
                    hasWordDocument = true;
                }

                if (hasContentTypes && hasWordDocument) {
                    return true;
                }
            }
        }

        return false;
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

    private record UploadResult(
            String originalFileName,
            String objectKey,
            String contentType,
            long size
    ) {
    }
}
