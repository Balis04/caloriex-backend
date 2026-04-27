package com.example.caloriexbackend.trainingrequest.email;

import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingRequestEmailServiceTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private TrainingRequestEmailTemplateBuilder templateBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private TrainingRequestEmailService trainingRequestEmailService;

    @BeforeEach
    void setUp() {
        trainingRequestEmailService = new TrainingRequestEmailService(restClientBuilder, templateBuilder);
        ReflectionTestUtils.setField(trainingRequestEmailService, "fromAddress", "noreply@caloriex.test");
        ReflectionTestUtils.setField(trainingRequestEmailService, "resendApiKey", "test-api-key");
        ReflectionTestUtils.setField(trainingRequestEmailService, "resendBaseUrl", "https://api.resend.test");
        ReflectionTestUtils.setField(trainingRequestEmailService, "creationTemplateId", "tpl-create");
        ReflectionTestUtils.setField(trainingRequestEmailService, "statusUpdateTemplateId", "tpl-status");
        ReflectionTestUtils.setField(trainingRequestEmailService, "trainingPlanTemplateId", "tpl-plan");
    }

    @Test
    void sendCreationEmailCoachEmailIsMissing() {
        User requester = user("Requester", "requester@example.com");
        User coachUser = user("Coach", null);
        CoachProfile coachProfile = coachProfile(coachUser);
        TrainingRequest request = trainingRequest(requester, coachProfile);

        trainingRequestEmailService.sendCreationEmail(request, requester, coachProfile);

        verify(templateBuilder, never()).buildCreationVariables(any(), any(), any());
        verify(restClientBuilder, never()).build();
    }

    @Test
    void sendStatusUpdateEmailRequesterEmailIsMissing() {
        User requester = user("Requester", " ");
        CoachProfile coachProfile = coachProfile(user("Coach", "coach@example.com"));
        TrainingRequest request = trainingRequest(requester, coachProfile);

        trainingRequestEmailService.sendStatusUpdateEmail(request);

        verify(templateBuilder, never()).buildStatusUpdateVariables(any());
        verify(restClientBuilder, never()).build();
    }

    @Test
    void sendCreationEmailSuccessfully() {
        User requester = user("Requester", "requester@example.com");
        User coachUser = user("Coach", "coach@example.com");
        CoachProfile coachProfile = coachProfile(coachUser);
        TrainingRequest request = trainingRequest(requester, coachProfile);
        Map<String, Object> variables = new HashMap<>();
        variables.put("coachName", "Coach");

        when(templateBuilder.buildCreationVariables(request, requester, coachProfile)).thenReturn(variables);
        when(restClientBuilder.baseUrl("https://api.resend.test")).thenReturn(restClientBuilder);
        when(restClientBuilder.defaultHeader("Authorization", "Bearer test-api-key")).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/emails")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(eq(org.springframework.http.MediaType.APPLICATION_JSON))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

        trainingRequestEmailService.sendCreationEmail(request, requester, coachProfile);

        verify(templateBuilder).buildCreationVariables(request, requester, coachProfile);
        verify(restClientBuilder).baseUrl("https://api.resend.test");
        verify(restClientBuilder).defaultHeader("Authorization", "Bearer test-api-key");
        verify(restClientBuilder).build();
        verify(requestBodyUriSpec).uri("/emails");
        verify(requestBodySpec).body(any(Object.class));
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void sendTrainingPlanUploadedEmailSuccessfully() {
        User requester = user("Requester", "requester@example.com");
        CoachProfile coachProfile = coachProfile(user("Coach", "coach@example.com"));
        TrainingRequest request = trainingRequest(requester, coachProfile);
        TrainingPlan trainingPlan = trainingPlan(request);
        Map<String, Object> variables = new HashMap<>();
        variables.put("planName", "Plan A");

        when(templateBuilder.buildTrainingPlanUploadedVariables(request, trainingPlan)).thenReturn(variables);
        when(restClientBuilder.baseUrl("https://api.resend.test")).thenReturn(restClientBuilder);
        when(restClientBuilder.defaultHeader("Authorization", "Bearer test-api-key")).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/emails")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(eq(org.springframework.http.MediaType.APPLICATION_JSON))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

        trainingRequestEmailService.sendTrainingPlanUploadedEmail(request, trainingPlan);

        verify(templateBuilder).buildTrainingPlanUploadedVariables(request, trainingPlan);
        verify(responseSpec).toBodilessEntity();
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

    private TrainingRequest trainingRequest(User requester, CoachProfile coachProfile) {
        TrainingRequest request = new TrainingRequest();
        request.setId(UUID.randomUUID());
        request.setRequesterUser(requester);
        request.setCoachProfile(coachProfile);
        return request;
    }

    private TrainingPlan trainingPlan(TrainingRequest request) {
        TrainingPlan trainingPlan = new TrainingPlan();
        trainingPlan.setId(UUID.randomUUID());
        trainingPlan.setTrainingRequest(request);
        trainingPlan.setPlanName("Plan A");
        trainingPlan.setUploadedAt(Instant.now());
        return trainingPlan;
    }
}

