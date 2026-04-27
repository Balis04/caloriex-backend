package com.example.caloriexbackend.trainingrequest.service;

import com.example.caloriexbackend.coachprofile.service.CoachProfileService;
import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import com.example.caloriexbackend.common.exception.BadRequestException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.trainingrequest.email.TrainingRequestEmailService;
import com.example.caloriexbackend.trainingrequest.mapper.TrainingRequestMapper;
import com.example.caloriexbackend.trainingrequest.payload.request.TrainingRequestCreateRequest;
import com.example.caloriexbackend.trainingrequest.payload.request.TrainingRequestStatusUpdateRequest;
import com.example.caloriexbackend.trainingrequest.payload.response.ClosedTrainingRequestResponse;
import com.example.caloriexbackend.trainingrequest.payload.response.TrainingRequestResponse;
import com.example.caloriexbackend.trainingrequest.repository.TrainingRequestRepository;
import com.example.caloriexbackend.trainingrequest.trainingplan.mapper.TrainingPlanMapper;
import com.example.caloriexbackend.trainingrequest.trainingplan.payload.TrainingPlanResponse;
import com.example.caloriexbackend.trainingrequest.trainingplan.repository.TrainingPlanRepository;
import com.example.caloriexbackend.trainingrequest.trainingplan.service.TrainingPlanService;
import com.example.caloriexbackend.validation.TrainingRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingRequestServiceTest {

    @Mock
    private TrainingRequestRepository trainingRequestRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private TrainingRequestEmailService emailService;

    @Mock
    private TrainingRequestMapper trainingRequestMapper;

    @Mock
    private TrainingPlanMapper trainingPlanMapper;

    @Mock
    private TrainingRequestValidator validator;

    @Mock
    private TrainingPlanService trainingPlanService;

    @Mock
    private CoachProfileService coachProfileService;

    @InjectMocks
    private TrainingRequestService trainingRequestService;

    @Test
    void createSuccessfully() {
        User requester = user("Requester", "requester@example.com");
        UUID coachProfileId = UUID.randomUUID();
        CoachProfile coachProfile = coachProfile(user("Coach", "coach@example.com"));
        TrainingRequestCreateRequest request = new TrainingRequestCreateRequest(3, 60, "Gym", "Need a hypertrophy plan");
        TrainingRequest trainingRequest = trainingRequest(requester, coachProfile, TrainingRequestStatus.PENDING);
        TrainingRequestResponse response = trainingRequestResponse(trainingRequest);

        when(authenticatedUserService.getUser()).thenReturn(requester);
        when(coachProfileService.findCoachProfile(coachProfileId)).thenReturn(coachProfile);
        when(trainingRequestMapper.toEntity(request, TrainingRequestStatus.PENDING, requester, coachProfile)).thenReturn(trainingRequest);
        when(trainingRequestRepository.save(trainingRequest)).thenReturn(trainingRequest);
        when(trainingRequestMapper.toResponse(trainingRequest)).thenReturn(response);

        TrainingRequestResponse actual = trainingRequestService.create(coachProfileId, request);

        assertSame(response, actual);
        verify(emailService).sendCreationEmail(trainingRequest, requester, coachProfile);
    }

    @Test
    void createOwnCoachProfile() {
        User requester = user("Requester", "requester@example.com");
        UUID coachProfileId = UUID.randomUUID();
        CoachProfile coachProfile = coachProfile(requester);
        TrainingRequestCreateRequest request = new TrainingRequestCreateRequest(3, 60, "Gym", "Need a hypertrophy plan");

        when(authenticatedUserService.getUser()).thenReturn(requester);
        when(coachProfileService.findCoachProfile(coachProfileId)).thenReturn(coachProfile);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> trainingRequestService.create(coachProfileId, request)
        );

        assertEquals("You cannot send a training request to your own coach profile", exception.getMessage());
        verify(trainingRequestRepository, never()).save(any());
    }

    @Test
    void updateStatusSuccessfully() {
        User coachUser = user("Coach", "coach@example.com");
        UUID requestId = UUID.randomUUID();
        TrainingRequest trainingRequest = trainingRequest(user("Requester", "requester@example.com"), coachProfile(coachUser), TrainingRequestStatus.PENDING);
        trainingRequest.setId(requestId);
        TrainingRequestStatusUpdateRequest request = new TrainingRequestStatusUpdateRequest(
                TrainingRequestStatus.APPROVED,
                "Sounds good"
        );
        TrainingRequestResponse response = trainingRequestResponse(trainingRequest);

        when(authenticatedUserService.getUser()).thenReturn(coachUser);
        when(trainingRequestRepository.findByIdAndCoachProfileUserId(requestId, coachUser.getId())).thenReturn(Optional.of(trainingRequest));
        when(trainingRequestMapper.toResponse(trainingRequest)).thenReturn(response);

        TrainingRequestResponse actual = trainingRequestService.updateStatus(requestId, request);

        assertSame(response, actual);
        assertEquals(TrainingRequestStatus.APPROVED, trainingRequest.getStatus());
        assertEquals("Sounds good", trainingRequest.getCoachResponse());
        verify(validator).validateStatusUpdate(TrainingRequestStatus.PENDING, TrainingRequestStatus.APPROVED);
        verify(emailService).sendStatusUpdateEmail(trainingRequest);
    }

    @Test
    void uploadTrainingPlanSuccessfully() {
        User coachUser = user("Coach", "coach@example.com");
        UUID requestId = UUID.randomUUID();
        TrainingRequest trainingRequest = trainingRequest(user("Requester", "requester@example.com"), coachProfile(coachUser), TrainingRequestStatus.APPROVED);
        trainingRequest.setId(requestId);
        MockMultipartFile file = new MockMultipartFile("file", "plan.pdf", "application/pdf", "%PDF".getBytes());
        TrainingPlan trainingPlan = trainingPlan(trainingRequest);
        TrainingPlanResponse response = new TrainingPlanResponse("Plan A", "Desc", "plan.pdf", Instant.now());

        when(authenticatedUserService.getUser()).thenReturn(coachUser);
        when(trainingRequestRepository.findByIdAndCoachProfileUserId(requestId, coachUser.getId())).thenReturn(Optional.of(trainingRequest));
        when(trainingPlanRepository.existsByTrainingRequestId(requestId)).thenReturn(false);
        when(trainingPlanService.create(trainingRequest, file, "Plan A", "Desc")).thenReturn(trainingPlan);
        when(trainingPlanMapper.toResponse(trainingPlan)).thenReturn(response);

        TrainingPlanResponse actual = trainingRequestService.uploadTrainingPlan(requestId, file, "Plan A", "Desc");

        assertSame(response, actual);
        assertEquals(TrainingRequestStatus.CLOSED, trainingRequest.getStatus());
        verify(validator).validateUpload(trainingRequest, false);
        verify(emailService).sendTrainingPlanUploadedEmail(trainingRequest, trainingPlan);
    }

    @Test
    void getMyRequestsSuccessfully() {
        User requester = user("Requester", "requester@example.com");
        TrainingRequest first = trainingRequest(requester, coachProfile(user("Coach 1", "coach1@example.com")), TrainingRequestStatus.PENDING);
        TrainingRequest second = trainingRequest(requester, coachProfile(user("Coach 2", "coach2@example.com")), TrainingRequestStatus.APPROVED);
        TrainingRequestResponse firstResponse = trainingRequestResponse(first);
        TrainingRequestResponse secondResponse = trainingRequestResponse(second);

        when(authenticatedUserService.getUser()).thenReturn(requester);
        when(trainingRequestRepository.findAllByRequesterUserIdOrderByCreatedAtDesc(requester.getId())).thenReturn(List.of(first, second));
        when(trainingRequestMapper.toResponse(first)).thenReturn(firstResponse);
        when(trainingRequestMapper.toResponse(second)).thenReturn(secondResponse);

        List<TrainingRequestResponse> actual = trainingRequestService.getMyRequests();

        assertEquals(List.of(firstResponse, secondResponse), actual);
    }

    @Test
    void getRequestsForMyCoachProfileStatusIsNull() {
        User coachUser = user("Coach", "coach@example.com");
        TrainingRequest trainingRequest = trainingRequest(user("Requester", "requester@example.com"), coachProfile(coachUser), TrainingRequestStatus.PENDING);
        TrainingRequestResponse response = trainingRequestResponse(trainingRequest);

        when(authenticatedUserService.getUser()).thenReturn(coachUser);
        when(trainingRequestRepository.findAllByCoachProfileUserIdOrderByCreatedAtDesc(coachUser.getId())).thenReturn(List.of(trainingRequest));
        when(trainingRequestMapper.toResponse(trainingRequest)).thenReturn(response);

        List<TrainingRequestResponse> actual = trainingRequestService.getRequestsForMyCoachProfile(null);

        assertEquals(List.of(response), actual);
        verify(trainingRequestRepository).findAllByCoachProfileUserIdOrderByCreatedAtDesc(coachUser.getId());
    }

    @Test
    void getRequestsForMyCoachProfileStatusIsProvided() {
        User coachUser = user("Coach", "coach@example.com");
        TrainingRequest trainingRequest = trainingRequest(user("Requester", "requester@example.com"), coachProfile(coachUser), TrainingRequestStatus.APPROVED);
        TrainingRequestResponse response = trainingRequestResponse(trainingRequest);

        when(authenticatedUserService.getUser()).thenReturn(coachUser);
        when(trainingRequestRepository.findAllByCoachProfileUserIdAndStatusOrderByCreatedAtDesc(
                coachUser.getId(),
                TrainingRequestStatus.APPROVED
        )).thenReturn(List.of(trainingRequest));
        when(trainingRequestMapper.toResponse(trainingRequest)).thenReturn(response);

        List<TrainingRequestResponse> actual = trainingRequestService.getRequestsForMyCoachProfile(TrainingRequestStatus.APPROVED);

        assertEquals(List.of(response), actual);
    }

    @Test
    void getClosedRequestsForMyCoachProfileSuccessfully() {
        User coachUser = user("Coach", "coach@example.com");
        TrainingRequest trainingRequest = trainingRequest(user("Requester", "requester@example.com"), coachProfile(coachUser), TrainingRequestStatus.CLOSED);
        TrainingPlan trainingPlan = trainingPlan(trainingRequest);
        ClosedTrainingRequestResponse response = new ClosedTrainingRequestResponse(
                UUID.randomUUID(), "Coach", "Requester", "requester@example.com",
                3, 60, "Gym", "CLOSED", "Need a hypertrophy plan", "Done",
                Instant.now(), "Plan A", "Desc", "plan.pdf", Instant.now()
        );

        when(authenticatedUserService.getUser()).thenReturn(coachUser);
        when(trainingPlanRepository.findAllByTrainingRequestCoachProfileUserIdOrderByUploadedAtDesc(coachUser.getId()))
                .thenReturn(List.of(trainingPlan));
        when(trainingRequestMapper.toClosedResponse(trainingPlan)).thenReturn(response);

        List<ClosedTrainingRequestResponse> actual = trainingRequestService.getClosedRequestsForMyCoachProfile();

        assertEquals(List.of(response), actual);
    }

    private User user(String fullName, String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName(fullName);
        user.setEmail(email);
        return user;
    }

    private CoachProfile coachProfile(User user) {
        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setId(UUID.randomUUID());
        coachProfile.setUser(user);
        return coachProfile;
    }

    private TrainingRequest trainingRequest(User requester, CoachProfile coachProfile, TrainingRequestStatus status) {
        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setId(UUID.randomUUID());
        trainingRequest.setRequesterUser(requester);
        trainingRequest.setCoachProfile(coachProfile);
        trainingRequest.setWeeklyTrainingCount(3);
        trainingRequest.setSessionDurationMinutes(60);
        trainingRequest.setPreferredLocation("Gym");
        trainingRequest.setRequestDescription("Need a hypertrophy plan");
        trainingRequest.setCoachResponse("Done");
        trainingRequest.setStatus(status);
        trainingRequest.setCreatedAt(Instant.now());
        return trainingRequest;
    }

    private TrainingPlan trainingPlan(TrainingRequest trainingRequest) {
        TrainingPlan trainingPlan = new TrainingPlan();
        trainingPlan.setTrainingRequest(trainingRequest);
        trainingPlan.setPlanName("Plan A");
        trainingPlan.setPlanDescription("Desc");
        trainingPlan.setFileName("plan.pdf");
        trainingPlan.setStorageKey("plans/key");
        trainingPlan.setContentType("application/pdf");
        trainingPlan.setUploadedAt(Instant.now());
        return trainingPlan;
    }

    private TrainingRequestResponse trainingRequestResponse(TrainingRequest trainingRequest) {
        return new TrainingRequestResponse(
                trainingRequest.getId(),
                trainingRequest.getCoachProfile().getId(),
                trainingRequest.getRequesterUser().getId(),
                trainingRequest.getCoachProfile().getUser().getFullName(),
                trainingRequest.getRequesterUser().getFullName(),
                trainingRequest.getRequesterUser().getEmail(),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                trainingRequest.getPreferredLocation(),
                trainingRequest.getStatus(),
                trainingRequest.getRequestDescription(),
                trainingRequest.getCoachResponse(),
                trainingRequest.getCreatedAt()
        );
    }
}
