package com.example.caloriexbackend.coachprofile.service;

import com.example.caloriexbackend.coachprofile.coachavailability.CoachAvailabilityFactory;
import com.example.caloriexbackend.coachprofile.coachavailability.CoachAvailabilityRepository;
import com.example.caloriexbackend.coachprofile.coachavailability.payload.CoachAvailabilityRequest;
import com.example.caloriexbackend.coachprofile.coachcertificate.CoachCertificateFactory;
import com.example.caloriexbackend.coachprofile.coachcertificate.CoachCertificateMapper;
import com.example.caloriexbackend.coachprofile.coachcertificate.CoachCertificateRepository;
import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateResponse;
import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateUploadRequest;
import com.example.caloriexbackend.coachprofile.mapper.CoachProfileMapper;
import com.example.caloriexbackend.coachprofile.payload.CoachListResponse;
import com.example.caloriexbackend.coachprofile.payload.CoachProfileRequest;
import com.example.caloriexbackend.coachprofile.payload.CoachProfileResponse;
import com.example.caloriexbackend.coachprofile.repository.CoachProfileRepository;
import com.example.caloriexbackend.common.enums.Currency;
import com.example.caloriexbackend.common.enums.TrainingFormat;
import com.example.caloriexbackend.common.exception.BadRequestException;
import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.CoachAvailability;
import com.example.caloriexbackend.entities.CoachCertificate;
import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.storage.StorageService;
import com.example.caloriexbackend.storage.payload.PublicDocumentUploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoachProfileServiceTest {

    @Mock
    private CoachProfileRepository coachProfileRepository;

    @Mock
    private CoachCertificateRepository coachCertificateRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private StorageService storageService;

    @Mock
    private CoachProfileMapper coachProfileMapper;

    @Mock
    private CoachCertificateMapper coachCertificateMapper;

    @Mock
    private CoachCertificateFactory coachCertificateFactory;

    @Mock
    private CoachAvailabilityFactory coachAvailabilityFactory;

    @Mock
    private CoachAvailabilityRepository coachAvailabilityRepository;

    @InjectMocks
    private CoachProfileService coachProfileService;

    @Test
    void createShouldSaveProfileForAuthenticatedUser() {
        User user = user(UUID.randomUUID());
        CoachProfileRequest request = coachProfileRequest(List.of(availabilityRequest(DayOfWeek.MONDAY)));
        CoachAvailability availability = new CoachAvailability();
        CoachProfile savedProfile = new CoachProfile();
        savedProfile.setId(UUID.randomUUID());
        CoachProfileResponse response = response(savedProfile.getId());

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.existsByUserId(user.getId())).thenReturn(false);
        when(coachAvailabilityFactory.createList(any(CoachProfile.class), any())).thenReturn(List.of(availability));
        when(coachProfileRepository.save(any(CoachProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(coachProfileMapper.toResponse(any(CoachProfile.class))).thenReturn(response);

        CoachProfileResponse actual = coachProfileService.create(request);

        ArgumentCaptor<CoachProfile> captor = ArgumentCaptor.forClass(CoachProfile.class);
        verify(coachProfileMapper).updateFromRequest(any(), captor.capture());
        verify(coachAvailabilityFactory).createList(captor.getValue(), request.getAvailabilities());
        verify(coachProfileRepository).save(captor.getValue());
        assertSame(response, actual);
        assertSame(user, captor.getValue().getUser());
        assertEquals(1, captor.getValue().getAvailabilities().size());
        assertSame(availability, captor.getValue().getAvailabilities().getFirst());
    }

    @Test
    void createShouldThrowWhenCurrentUserAlreadyHasProfile() {
        User user = user(UUID.randomUUID());
        CoachProfileRequest request = coachProfileRequest(List.of(availabilityRequest(DayOfWeek.MONDAY)));

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.existsByUserId(user.getId())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> coachProfileService.create(request));

        assertEquals("Coach profile already exists for the current user", exception.getMessage());
        verify(coachProfileRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenAvailabilitiesContainDuplicateDay() {
        User user = user(UUID.randomUUID());
        CoachProfileRequest request = coachProfileRequest(List.of(
                availabilityRequest(DayOfWeek.MONDAY),
                availabilityRequest(DayOfWeek.MONDAY)
        ));

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.existsByUserId(user.getId())).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> coachProfileService.create(request));

        assertEquals("Duplicate availability for day: MONDAY", exception.getMessage());
        verify(coachAvailabilityFactory, never()).createList(any(), any());
        verify(coachProfileRepository, never()).save(any());
    }

    @Test
    void updateShouldReplaceAvailabilitiesAndReturnResponse() {
        User user = user(UUID.randomUUID());
        UUID profileId = UUID.randomUUID();
        CoachProfile existingProfile = new CoachProfile();
        existingProfile.setId(profileId);
        existingProfile.getAvailabilities().add(new CoachAvailability());

        CoachProfileRequest request = coachProfileRequest(List.of(availabilityRequest(DayOfWeek.TUESDAY)));
        CoachAvailability newAvailability = new CoachAvailability();
        CoachProfileResponse response = response(profileId);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.findByIdAndUserId(profileId, user.getId())).thenReturn(Optional.of(existingProfile));
        when(coachAvailabilityFactory.createList(existingProfile, request.getAvailabilities())).thenReturn(List.of(newAvailability));
        when(coachProfileRepository.save(existingProfile)).thenReturn(existingProfile);
        when(coachProfileMapper.toResponse(existingProfile)).thenReturn(response);

        CoachProfileResponse actual = coachProfileService.update(profileId, request);

        assertSame(response, actual);
        verify(coachProfileMapper).updateFromRequest(request, existingProfile);
        verify(coachAvailabilityRepository).deleteAllByCoachProfileId(profileId);
        assertEquals(1, existingProfile.getAvailabilities().size());
        assertSame(newAvailability, existingProfile.getAvailabilities().getFirst());
    }

    @Test
    void getMineShouldReturnMappedProfile() {
        User user = user(UUID.randomUUID());
        CoachProfile profile = new CoachProfile();
        CoachProfileResponse response = response(UUID.randomUUID());

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.findByUserId(user.getId())).thenReturn(Optional.of(profile));
        when(coachProfileMapper.toResponse(profile)).thenReturn(response);

        CoachProfileResponse actual = coachProfileService.getMine();

        assertSame(response, actual);
    }

    @Test
    void getAllShouldMapProfilesExceptAuthenticatedUsersOwn() {
        User user = user(UUID.randomUUID());
        CoachProfile first = new CoachProfile();
        CoachProfile second = new CoachProfile();
        CoachListResponse firstResponse = new CoachListResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "First Coach",
                "first@example.com",
                LocalDate.of(2020, 1, 1),
                "Desc",
                TrainingFormat.ONLINE,
                10,
                20,
                Currency.HUF,
                5,
                "Contact",
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );
        CoachListResponse secondResponse = new CoachListResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Second Coach",
                "second@example.com",
                LocalDate.of(2021, 1, 1),
                "Desc2",
                TrainingFormat.HYBRID,
                30,
                40,
                Currency.EUR,
                3,
                "Contact",
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.findAllByUserIdNot(user.getId())).thenReturn(List.of(first, second));
        when(coachProfileMapper.toListResponse(first)).thenReturn(firstResponse);
        when(coachProfileMapper.toListResponse(second)).thenReturn(secondResponse);

        List<CoachListResponse> actual = coachProfileService.getAll();

        assertEquals(List.of(firstResponse, secondResponse), actual);
    }

    @Test
    void uploadCertificateShouldUploadSaveAndReturnMappedResponse() {
        User user = user(UUID.randomUUID());
        UUID profileId = UUID.randomUUID();
        CoachProfile profile = new CoachProfile();
        profile.setId(profileId);

        CoachCertificateUploadRequest request = new CoachCertificateUploadRequest();
        request.setFile(new MockMultipartFile("file", "certificate.pdf", "application/pdf", "%PDF".getBytes()));
        request.setCertificateName("NASM");

        PublicDocumentUploadResponse uploadResponse = new PublicDocumentUploadResponse(
                "certificate.pdf",
                "https://cdn.example/certificate.pdf",
                "application/pdf",
                123L
        );
        CoachCertificate certificate = new CoachCertificate();
        CoachCertificateResponse response = new CoachCertificateResponse(
                UUID.randomUUID(),
                "NASM",
                "Issuer",
                Instant.now(),
                "https://cdn.example/certificate.pdf",
                Instant.now()
        );

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.findByIdAndUserId(profileId, user.getId())).thenReturn(Optional.of(profile));
        when(storageService.uploadCertificate(request.getFile())).thenReturn(uploadResponse);
        when(coachCertificateFactory.create(request, profile, uploadResponse)).thenReturn(certificate);
        when(coachCertificateRepository.save(certificate)).thenReturn(certificate);
        when(coachCertificateMapper.toResponse(certificate)).thenReturn(response);

        CoachCertificateResponse actual = coachProfileService.uploadCertificate(profileId, request);

        assertSame(response, actual);
        assertEquals(1, profile.getCertificates().size());
        assertSame(certificate, profile.getCertificates().getFirst());
    }

    @Test
    void deleteCertificateShouldDeleteStoredFileAndEntity() {
        User user = user(UUID.randomUUID());
        UUID profileId = UUID.randomUUID();
        UUID certificateId = UUID.randomUUID();
        CoachCertificate certificate = new CoachCertificate();
        certificate.setFileUrl("https://cdn.example/certificate.pdf");

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachCertificateRepository.findByIdAndCoachProfileIdAndCoachProfileUserId(
                certificateId,
                profileId,
                user.getId()
        )).thenReturn(Optional.of(certificate));
        doNothing().when(storageService).deleteCertificate(certificate.getFileUrl());

        coachProfileService.deleteCertificate(profileId, certificateId);

        verify(storageService).deleteCertificate(certificate.getFileUrl());
        verify(coachCertificateRepository).delete(certificate);
    }

    @Test
    void deleteCertificateShouldThrowWhenCertificateIsMissing() {
        User user = user(UUID.randomUUID());
        UUID profileId = UUID.randomUUID();
        UUID certificateId = UUID.randomUUID();

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachCertificateRepository.findByIdAndCoachProfileIdAndCoachProfileUserId(
                certificateId,
                profileId,
                user.getId()
        )).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> coachProfileService.deleteCertificate(profileId, certificateId)
        );

        assertEquals("Certificate not found for the current coach profile", exception.getMessage());
        verify(storageService, never()).deleteCertificate(any());
        verify(coachCertificateRepository, never()).delete(any());
    }

    @Test
    void deleteShouldRemoveAuthenticatedUsersProfile() {
        User user = user(UUID.randomUUID());
        UUID profileId = UUID.randomUUID();
        CoachProfile profile = new CoachProfile();
        profile.setId(profileId);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(coachProfileRepository.findByIdAndUserId(profileId, user.getId())).thenReturn(Optional.of(profile));

        coachProfileService.delete(profileId);

        verify(coachProfileRepository).delete(profile);
    }

    @Test
    void findCoachProfileShouldReturnProfileWhenItExists() {
        UUID profileId = UUID.randomUUID();
        CoachProfile profile = new CoachProfile();
        profile.setId(profileId);

        when(coachProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        CoachProfile actual = coachProfileService.findCoachProfile(profileId);

        assertSame(profile, actual);
    }

    @Test
    void findCoachProfileShouldThrowWhenItDoesNotExist() {
        UUID profileId = UUID.randomUUID();
        when(coachProfileRepository.findById(profileId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> coachProfileService.findCoachProfile(profileId)
        );

        assertEquals("Coach profile not found", exception.getMessage());
    }

    private User user(UUID id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private CoachProfileRequest coachProfileRequest(List<CoachAvailabilityRequest> availabilities) {
        CoachProfileRequest request = new CoachProfileRequest();
        request.setTrainingStartedAt(LocalDate.of(2020, 1, 1));
        request.setShortDescription("Test coach");
        request.setTrainingFormat(TrainingFormat.ONLINE);
        request.setPriceFrom(1000);
        request.setPriceTo(2000);
        request.setCurrency(Currency.HUF);
        request.setMaxCapacity(10);
        request.setContactNote("Reach out");
        request.setAvailabilities(availabilities);
        return request;
    }

    private CoachAvailabilityRequest availabilityRequest(DayOfWeek dayOfWeek) {
        CoachAvailabilityRequest request = new CoachAvailabilityRequest();
        request.setDayOfWeek(dayOfWeek);
        request.setAvailable(true);
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(17, 0));
        return request;
    }

    private CoachProfileResponse response(UUID id) {
        assertNotNull(id);
        return new CoachProfileResponse(
                id,
                UUID.randomUUID(),
                LocalDate.of(2020, 1, 1),
                "Test coach",
                TrainingFormat.ONLINE,
                1000,
                2000,
                Currency.HUF,
                10,
                "Reach out",
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );
    }
}


