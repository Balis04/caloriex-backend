package com.example.caloriexbackend.trainingrequest.email;

import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingRequestEmailService {

    private final RestClient.Builder restClientBuilder;
    private final TrainingRequestEmailTemplateBuilder templateBuilder;

    @Value("${app.mail.training-request.from:${spring.mail.username:}}")
    private String fromAddress;

    @Value("${app.mail.resend.api-key:}")
    private String resendApiKey;

    @Value("${app.mail.resend.base-url:https://api.resend.com}")
    private String resendBaseUrl;

    @Value("${app.mail.training-request.creation-template-id:}")
    private String creationTemplateId;

    @Value("${app.mail.training-request.status-update-template-id:${app.mail.training-request.creation-template-id:}}")
    private String statusUpdateTemplateId;

    @Value("${app.mail.training-request.training-plan-template-id:}")
    private String trainingPlanTemplateId;

    public void sendCreationEmail(TrainingRequest request, User requester, CoachProfile coachProfile) {
        String coachEmail = coachProfile.getUser().getEmail();

        if (coachEmail == null || coachEmail.isBlank()) {
            log.warn("Skipping creation email: coach email is missing, request id={}", request.getId());
            return;
        }

        sendCreationEmailWithTemplate(request, requester, coachProfile, coachEmail.trim());
    }

    public void sendStatusUpdateEmail(TrainingRequest request) {
        String requesterEmail = request.getRequesterUser().getEmail();

        if (requesterEmail == null || requesterEmail.isBlank()) {
            log.warn("Skipping status update email: requester email is missing, request id={}", request.getId());
            return;
        }

        sendStatusUpdateEmailWithTemplate(request, requesterEmail.trim());
    }

    public void sendTrainingPlanUploadedEmail(TrainingRequest request, TrainingPlan trainingPlan) {
        String requesterEmail = request.getRequesterUser().getEmail();

        if (requesterEmail == null || requesterEmail.isBlank()) {
            log.warn("Skipping training plan uploaded email: requester email is missing, request id={}", request.getId());
            return;
        }

        sendTrainingPlanUploadedEmailWithTemplate(request, trainingPlan, requesterEmail.trim());
    }

    private void sendCreationEmailWithTemplate(
            TrainingRequest request,
            User requester,
            CoachProfile coachProfile,
            String coachEmail
    ) {
        if (creationTemplateId == null || creationTemplateId.isBlank()) {
            log.warn("Skipping creation email: template id is missing, request id={}", request.getId());
            return;
        }

        if (resendApiKey == null || resendApiKey.isBlank()) {
            log.warn("Skipping creation email: Resend API key is missing, request id={}", request.getId());
            return;
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Skipping Resend template send: from address is missing, request id={}", request.getId());
            return;
        }

        Map<String, Object> variables = templateBuilder.buildCreationVariables(request, requester, coachProfile);
        variables.put("subject", buildCreationSubject(requester));
        String replyTo = requester.getEmail();

        try {
            resendClient().post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ResendEmailRequest(
                            fromAddress.trim(),
                            List.of(coachEmail),
                            buildCreationSubject(requester),
                            replyTo != null && !replyTo.isBlank() ? replyTo.trim() : null,
                            new ResendTemplate(creationTemplateId.trim(), variables)
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException | ResourceAccessException ex) {
            log.warn("Failed to send training request creation email via Resend template to coach '{}', request id={}",
                    coachEmail, request.getId(), ex);
        }
    }

    private void sendStatusUpdateEmailWithTemplate(TrainingRequest request, String requesterEmail) {
        if (statusUpdateTemplateId == null || statusUpdateTemplateId.isBlank()) {
            log.warn("Skipping status update email: template id is missing, request id={}", request.getId());
            return;
        }

        if (resendApiKey == null || resendApiKey.isBlank()) {
            log.warn("Skipping status update email: Resend API key is missing, request id={}", request.getId());
            return;
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Skipping Resend template send: from address is missing, request id={}", request.getId());
            return;
        }

        Map<String, Object> variables = templateBuilder.buildStatusUpdateVariables(request);
        variables.put("subject", buildStatusUpdateSubject(request));

        try {
            resendClient().post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ResendEmailRequest(
                            fromAddress.trim(),
                            List.of(requesterEmail),
                            buildStatusUpdateSubject(request),
                            null,
                            new ResendTemplate(statusUpdateTemplateId.trim(), variables)
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException | ResourceAccessException ex) {
            log.warn("Failed to send training request status update email via Resend template to requester '{}', request id={}",
                    requesterEmail, request.getId(), ex);
        }
    }

    private void sendTrainingPlanUploadedEmailWithTemplate(
            TrainingRequest request,
            TrainingPlan trainingPlan,
            String requesterEmail
    ) {
        if (trainingPlanTemplateId == null || trainingPlanTemplateId.isBlank()) {
            log.warn("Skipping training plan uploaded email: template id is missing, request id={}", request.getId());
            return;
        }

        if (resendApiKey == null || resendApiKey.isBlank()) {
            log.warn("Skipping training plan uploaded email: Resend API key is missing, request id={}", request.getId());
            return;
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Skipping Resend template send: from address is missing, request id={}", request.getId());
            return;
        }

        Map<String, Object> variables = templateBuilder.buildTrainingPlanUploadedVariables(request, trainingPlan);
        variables.put("subject", buildTrainingPlanUploadedSubject(request));

        try {
            resendClient().post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ResendEmailRequest(
                            fromAddress.trim(),
                            List.of(requesterEmail),
                            buildTrainingPlanUploadedSubject(request),
                            null,
                            new ResendTemplate(trainingPlanTemplateId.trim(), variables)
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException | ResourceAccessException ex) {
            log.warn("Failed to send training plan uploaded email via Resend template to requester '{}', request id={}",
                    requesterEmail, request.getId(), ex);
        }
    }

    private RestClient resendClient() {
        return restClientBuilder
                .baseUrl(resendBaseUrl)
                .defaultHeader("Authorization", "Bearer " + resendApiKey.trim())
                .build();
    }

    private String buildCreationSubject(User requester) {
        return "[Caloriex] New training request received from " + requester.getFullName();
    }

    private String buildStatusUpdateSubject(TrainingRequest request) {
        return "[Caloriex] Your training request status was updated by "
                + request.getCoachProfile().getUser().getFullName();
    }

    private String buildTrainingPlanUploadedSubject(TrainingRequest request) {
        return "[Caloriex] Your training plan is now available from "
                + request.getCoachProfile().getUser().getFullName();
    }

    private record ResendEmailRequest(
            String from,
            List<String> to,
            String subject,
            String reply_to,
            ResendTemplate template
    ) {
    }

    private record ResendTemplate(
            String id,
            Map<String, Object> variables
    ) {
    }
}
