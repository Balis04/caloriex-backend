package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.coachprofile.coachavailability.CoachAvailabilityFactory;
import com.example.caloryxbackend.coachprofile.coachavailability.CoachAvailabilityRepository;
import com.example.caloryxbackend.coachprofile.coachcertificate.CoachCertificateFactory;
import com.example.caloryxbackend.coachprofile.coachcertificate.CoachCertificateMapper;
import com.example.caloryxbackend.coachprofile.coachcertificate.CoachCertificateRepository;
import com.example.caloryxbackend.coachprofile.coachcertificate.payload.CoachCertificateResponse;
import com.example.caloryxbackend.coachprofile.coachcertificate.payload.CoachCertificateUploadRequest;
import com.example.caloryxbackend.coachprofile.payload.CoachListResponse;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileRequest;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileResponse;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.CoachCertificate;
import com.example.caloryxbackend.entities.CoachProfile;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.storage.CertificateStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoachProfileService {

    private final CoachProfileRepository coachProfileRepository;
    private final CoachCertificateRepository coachCertificateRepository;
    private final CurrentUserService currentUserService;
    private final CertificateStorageService certificateStorageService;
    private final CoachProfileMapper coachProfileMapper;
    private final CoachCertificateMapper coachCertificateMapper;
    private final CoachCertificateFactory coachCertificateFactory;
    private final CoachAvailabilityFactory coachAvailabilityFactory;
    private final CoachAvailabilityRepository coachAvailabilityRepository;

    @Transactional
    public CoachProfileResponse create(CoachProfileRequest request) {

        User user = currentUserService.getUser();

        if (coachProfileRepository.existsByUserId(user.getId())) {
            throw new BadRequestException("Coach profile already exists for the current user");
        }

        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setUser(user);

        coachProfileMapper.updateFromRequest(request, coachProfile);

        validateAvailabilities(request);
        replaceAvailability(coachProfile, request);

        CoachProfile saved = coachProfileRepository.save(coachProfile);
        return coachProfileMapper.toResponse(saved);
    }

    @Transactional
    public CoachProfileResponse update(UUID id, CoachProfileRequest request) {

        CoachProfile coachProfile = getMyCoachProfile(id);

        coachProfileMapper.updateFromRequest(request, coachProfile);

        validateAvailabilities(request);

        replaceAvailability(coachProfile, request);

        CoachProfile saved = coachProfileRepository.save(coachProfile);
        return coachProfileMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CoachProfileResponse getMine() {
        CoachProfile coachProfile = getMyCoachProfile();

        return coachProfileMapper.toResponse(coachProfile);
    }

    @Transactional(readOnly = true)
    public List<CoachListResponse> getAll() {
        User user = currentUserService.getUser();
        return coachProfileRepository.findAllByUserIdNot(user.getId()).stream()
                .map(coachProfileMapper::toListResponse)
                .toList();
    }

    @Transactional
    public CoachCertificateResponse uploadCertificate(
            UUID id,
            CoachCertificateUploadRequest request
    ) {
        CoachProfile coachProfile = getMyCoachProfile(id);

        var upload = certificateStorageService.uploadCertificate(request.getFile());

        CoachCertificate certificate = coachCertificateFactory.create(
                request,
                coachProfile,
                upload
        );

        CoachCertificate saved = coachCertificateRepository.save(certificate);
        coachProfile.getCertificates().add(saved);

        return coachCertificateMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        CoachProfile coachProfile = getMyCoachProfile(id);

        coachProfileRepository.delete(coachProfile);
    }

    private void validateAvailabilities(CoachProfileRequest request) {
        if (request.getAvailabilities() == null) return;

        Set<DayOfWeek> days = new HashSet<>();

        for (var a : request.getAvailabilities()) {
            if (!days.add(a.getDayOfWeek())) {
                throw new BadRequestException("Duplicate availability for day: " + a.getDayOfWeek());
            }
        }
    }

    private void replaceAvailability(CoachProfile profile, CoachProfileRequest request) {
        if (profile.getId() != null) {
            coachAvailabilityRepository.deleteAllByCoachProfileId(profile.getId());
        }

        profile.getAvailabilities().clear();

        profile.getAvailabilities().addAll(
                coachAvailabilityFactory.createList(profile, request.getAvailabilities())
        );
    }

    private CoachProfile getMyCoachProfile(UUID id) {
        User user = currentUserService.getUser();

        return coachProfileRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Coach profile does not exist for the current user"));
    }

    public CoachProfile findCoachProfile(UUID coachProfileId){
        return coachProfileRepository.findById(coachProfileId)
                .orElseThrow(() -> new NotFoundException("Coach profile not found"));
    }

    private CoachProfile getMyCoachProfile() {
        User user = currentUserService.getUser();

        return coachProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Coach profile does not exist for the current user"));
    }
}
