package com.example.caloriexbackend.trainingrequest.trainingplan.service;

import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.storage.StorageService;
import com.example.caloriexbackend.storage.payload.ProtectedDocumentUploadResponse;
import com.example.caloriexbackend.storage.payload.StoredFileDownload;
import com.example.caloriexbackend.trainingrequest.trainingplan.mapper.TrainingPlanMapper;
import com.example.caloriexbackend.trainingrequest.trainingplan.repository.TrainingPlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingPlanServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private TrainingPlanRepository repository;

    @Mock
    private TrainingPlanMapper trainingPlanMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private TrainingPlanService trainingPlanService;

    @Test
    void createSuccessfully() {
        TrainingRequest request = trainingRequest();
        MockMultipartFile file = new MockMultipartFile("file", "plan.pdf", "application/pdf", "%PDF".getBytes());
        ProtectedDocumentUploadResponse upload = new ProtectedDocumentUploadResponse(
                "plan.pdf",
                "training-plans/key",
                "application/pdf",
                123L
        );
        TrainingPlan trainingPlan = trainingPlan(request);

        when(storageService.uploadTrainingPlan(file)).thenReturn(upload);
        when(trainingPlanMapper.toEntity(request, "Plan A", "Desc", upload)).thenReturn(trainingPlan);
        when(repository.save(trainingPlan)).thenReturn(trainingPlan);

        TrainingPlan actual = trainingPlanService.create(request, file, "Plan A", "Desc");

        assertSame(trainingPlan, actual);
        verify(storageService).uploadTrainingPlan(file);
        verify(trainingPlanMapper).toEntity(request, "Plan A", "Desc", upload);
        verify(repository).save(trainingPlan);
    }

    @Test
    void downloadByTrainingRequestIdSuccessfully() {
        User coachUser = user();
        TrainingPlan trainingPlan = trainingPlan(trainingRequestWithUsers(coachUser, user()));
        StoredFileDownload download = new StoredFileDownload("plan.pdf", "application/pdf", new byte[]{1});

        when(repository.findByTrainingRequestId(trainingPlan.getTrainingRequest().getId())).thenReturn(Optional.of(trainingPlan));
        when(authenticatedUserService.getUser()).thenReturn(coachUser);
        when(storageService.downloadTrainingPlan("training-plans/key", "plan.pdf", "application/pdf")).thenReturn(download);

        StoredFileDownload actual = trainingPlanService.downloadByTrainingRequestId(trainingPlan.getTrainingRequest().getId());

        assertSame(download, actual);
    }

    @Test
    void downloadByTrainingRequestIdSuccessfullyRequester() {
        User requester = user();
        TrainingPlan trainingPlan = trainingPlan(trainingRequestWithUsers(user(), requester));
        StoredFileDownload download = new StoredFileDownload("plan.pdf", "application/pdf", new byte[]{1});

        when(repository.findByTrainingRequestId(trainingPlan.getTrainingRequest().getId())).thenReturn(Optional.of(trainingPlan));
        when(authenticatedUserService.getUser()).thenReturn(requester);
        when(storageService.downloadTrainingPlan("training-plans/key", "plan.pdf", "application/pdf")).thenReturn(download);

        StoredFileDownload actual = trainingPlanService.downloadByTrainingRequestId(trainingPlan.getTrainingRequest().getId());

        assertSame(download, actual);
    }

    @Test
    void downloadByTrainingRequestIdUnauthorized() {
        TrainingPlan trainingPlan = trainingPlan(trainingRequestWithUsers(user(), user()));
        UUID requestId = trainingPlan.getTrainingRequest().getId();

        when(repository.findByTrainingRequestId(requestId)).thenReturn(Optional.of(trainingPlan));
        when(authenticatedUserService.getUser()).thenReturn(user());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainingPlanService.downloadByTrainingRequestId(requestId)
        );

        assertEquals("Training plan not found", exception.getMessage());
        verify(storageService, never()).downloadTrainingPlan("training-plans/key", "plan.pdf", "application/pdf");
    }

    @Test
    void downloadByTrainingRequestIdPlanDoesNotExist() {
        UUID requestId = UUID.randomUUID();
        when(repository.findByTrainingRequestId(requestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainingPlanService.downloadByTrainingRequestId(requestId)
        );

        assertEquals("Training plan not found", exception.getMessage());
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private TrainingRequest trainingRequest() {
        return trainingRequestWithUsers(user(), user());
    }

    private TrainingRequest trainingRequestWithUsers(User coachUser, User requester) {
        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setId(UUID.randomUUID());
        coachProfile.setUser(coachUser);

        TrainingRequest request = new TrainingRequest();
        request.setId(UUID.randomUUID());
        request.setCoachProfile(coachProfile);
        request.setRequesterUser(requester);
        return request;
    }

    private TrainingPlan trainingPlan(TrainingRequest request) {
        TrainingPlan trainingPlan = new TrainingPlan();
        trainingPlan.setId(UUID.randomUUID());
        trainingPlan.setTrainingRequest(request);
        trainingPlan.setPlanName("Plan A");
        trainingPlan.setPlanDescription("Desc");
        trainingPlan.setFileName("plan.pdf");
        trainingPlan.setStorageKey("training-plans/key");
        trainingPlan.setContentType("application/pdf");
        trainingPlan.setUploadedAt(Instant.now());
        return trainingPlan;
    }
}
