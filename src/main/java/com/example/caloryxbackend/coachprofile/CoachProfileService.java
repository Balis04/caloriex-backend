package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.coachprofile.payload.*;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.CoachAvailability;
import com.example.caloryxbackend.entities.CoachCertificate;
import com.example.caloryxbackend.entities.CoachProfile;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CoachProfileService {

    private final CoachProfileRepository coachProfileRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public CoachProfileResponse create(CoachProfileRequest request) {

        User user = getCurrentUser();

        if (coachProfileRepository.existsByUserId(user.getId())) {
            throw new BadRequestException("Coach profile already exists for the current user");
        }

        validateRequest(request);

        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setUser(user);

        applyProfileFields(coachProfile, request);

        replaceChildren(coachProfile, request);

        CoachProfile saved = coachProfileRepository.save(coachProfile);

        return mapProfileResponse(saved);
    }

    @Transactional
    public CoachProfileResponse update(UUID id, CoachProfileRequest request) {

        User user = getCurrentUser();

        CoachProfile coachProfile = coachProfileRepository
                .findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Coach profile not found"));

        validateRequest(request);

        applyProfileFields(coachProfile, request);

        replaceChildren(coachProfile, request);

        return mapProfileResponse(coachProfile);
    }

    @Transactional(readOnly = true)
    public CoachProfileResponse getMine() {
        User user = getCurrentUser();
        CoachProfile coachProfile = coachProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Coach profile does not exist for the current user"));

        return mapProfileResponse(coachProfile);
    }

    @Transactional(readOnly = true)
    public List<CoachListResponse> getAll() {
        User user = getCurrentUser();
        return coachProfileRepository.findAllByUserIdNot(user.getId()).stream()
                .map(this::mapListResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        User user = getCurrentUser();
        CoachProfile coachProfile = coachProfileRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Coach profile not found"));

        coachProfileRepository.delete(coachProfile);
    }

    private User getCurrentUser() {
        String auth0Id = currentUserService.getAuth0Id();
        return userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void applyProfileFields(CoachProfile entity, CoachProfileRequest request) {
        entity.setTrainingStartedAt(request.getTrainingStartedAt());
        entity.setShortDescription(request.getShortDescription());
        entity.setTrainingFormat(request.getTrainingFormat());
        entity.setPriceFrom(request.getPriceFrom());
        entity.setPriceTo(request.getPriceTo());
        entity.setCurrency(request.getCurrency());
        entity.setMaxCapacity(request.getMaxCapacity());
        entity.setContactNote(request.getContactNote());
    }

    private void replaceChildren(CoachProfile profile, CoachProfileRequest request) {

        profile.getAvailabilities().clear();
        profile.getCertificates().clear();

        entityManager.flush();

        profile.getAvailabilities().addAll(
                buildAvailabilities(profile, request.getAvailabilities())
        );

        profile.getCertificates().addAll(
                buildCertificates(profile, request.getCertificates())
        );
    }

    private List<CoachAvailability> buildAvailabilities(
            CoachProfile coachProfile,
            List<CoachAvailabilityRequest> requests
    ) {

        List<CoachAvailability> availabilities = new ArrayList<>();

        for (CoachAvailabilityRequest request : requests) {

            if (!Boolean.TRUE.equals(request.getAvailable())) {
                continue;
            }

            CoachAvailability entity = new CoachAvailability();
            entity.setCoachProfile(coachProfile);
            entity.setDayOfWeek(request.getDayOfWeek());
            entity.setAvailable(true);
            entity.setStartTime(request.getStartTime());
            entity.setEndTime(request.getEndTime());

            availabilities.add(entity);
        }

        return availabilities;
    }

    private List<CoachCertificate> buildCertificates(
            CoachProfile coachProfile,
            List<CoachCertificateRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<CoachCertificate> certificates = new ArrayList<>();
        for (CoachCertificateRequest request : requests) {
            if (isBlank(request.getFileName()) && isBlank(request.getFileUrl())) {
                continue;
            }

            CoachCertificate entity = new CoachCertificate();
            entity.setCoachProfile(coachProfile);
            entity.setFileName(request.getFileName());
            entity.setCertificateName(request.getCertificateName());
            entity.setIssuer(request.getIssuer());
            entity.setIssuedAt(request.getIssuedAt());
            entity.setFileUrl(request.getFileUrl());
            entity.setContentType(request.getContentType());
            entity.setFileSizeBytes(request.getFileSizeBytes());
            certificates.add(entity);
        }
        return certificates;
    }

    private void validateRequest(CoachProfileRequest request) {
        if (request.getPriceFrom() != null && request.getPriceTo() != null && request.getPriceFrom() > request.getPriceTo()) {
            throw new BadRequestException("Price from cannot be greater than price to");
        }

        if ((request.getPriceFrom() != null || request.getPriceTo() != null) && request.getCurrency() == null) {
            throw new BadRequestException("Currency is required when a price range is provided");
        }

        validateAvailabilities(request.getAvailabilities());
        validateCertificates(request.getCertificates());
    }

    private void validateAvailabilities(List<CoachAvailabilityRequest> requests) {
        Set<DayOfWeek> seenDays = EnumSet.noneOf(DayOfWeek.class);
        for (CoachAvailabilityRequest request : requests) {
            if (!seenDays.add(request.getDayOfWeek())) {
                throw new BadRequestException("Each day of week can only appear once");
            }

            boolean available = Boolean.TRUE.equals(request.getAvailable());
            if (available && (request.getStartTime() == null || request.getEndTime() == null)) {
                throw new BadRequestException("Start and end time are required for available days");
            }

            if (available && !request.getStartTime().isBefore(request.getEndTime())) {
                throw new BadRequestException("Start time must be before end time");
            }
        }
    }

    private void validateCertificates(List<CoachCertificateRequest> requests) {
        if (requests == null) {
            return;
        }

        for (CoachCertificateRequest request : requests) {
            boolean hasAnyCertificateValue =
                    !isBlank(request.getFileName()) ||
                    !isBlank(request.getFileUrl()) ||
                    !isBlank(request.getCertificateName()) ||
                    !isBlank(request.getIssuer()) ||
                    request.getIssuedAt() != null ||
                    !isBlank(request.getContentType()) ||
                    request.getFileSizeBytes() != null;

            if (!hasAnyCertificateValue) {
                continue;
            }

            if (isBlank(request.getFileName()) || isBlank(request.getFileUrl())) {
                throw new BadRequestException("Certificate fileName and fileUrl are required when certificate data is provided");
            }
        }
    }

    private CoachProfileResponse mapProfileResponse(CoachProfile entity) {
        return new CoachProfileResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTrainingStartedAt(),
                entity.getShortDescription(),
                entity.getTrainingFormat(),
                entity.getPriceFrom(),
                entity.getPriceTo(),
                entity.getCurrency(),
                entity.getMaxCapacity(),
                entity.getContactNote(),
                mapAvailabilities(entity),
                mapCertificates(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CoachListResponse mapListResponse(CoachProfile entity) {
        return new CoachListResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getFullName(),
                entity.getUser().getEmail(),
                entity.getTrainingStartedAt(),
                entity.getShortDescription(),
                entity.getTrainingFormat(),
                entity.getPriceFrom(),
                entity.getPriceTo(),
                entity.getCurrency(),
                entity.getMaxCapacity(),
                entity.getContactNote(),
                mapAvailabilities(entity),
                mapCertificates(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private List<CoachAvailabilityResponse> mapAvailabilities(CoachProfile entity) {
        return entity.getAvailabilities().stream()
                .sorted(Comparator.comparing(CoachAvailability::getDayOfWeek))
                .map(availability -> new CoachAvailabilityResponse(
                        availability.getId(),
                        availability.getDayOfWeek(),
                        availability.isAvailable(),
                        availability.getStartTime(),
                        availability.getEndTime()
                ))
                .toList();
    }

    private List<CoachCertificateResponse> mapCertificates(CoachProfile entity) {
        return entity.getCertificates().stream()
                .map(certificate -> new CoachCertificateResponse(
                        certificate.getId(),
                        certificate.getFileName(),
                        certificate.getCertificateName(),
                        certificate.getIssuer(),
                        certificate.getIssuedAt(),
                        certificate.getFileUrl(),
                        certificate.getContentType(),
                        certificate.getFileSizeBytes(),
                        certificate.getUploadedAt()
                ))
                .toList();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
