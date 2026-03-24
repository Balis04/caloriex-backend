package com.example.caloriexbackend.trainingrequest.email;

import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingRequestEmailService {

    private final JavaMailSender mailSender;
    private final TrainingRequestEmailTemplateBuilder templateBuilder;

    @Value("${app.mail.training-request.from:${spring.mail.username:}}")
    private String fromAddress;

    public void sendCreationEmail(TrainingRequest request, User requester, CoachProfile coachProfile) {
        String coachEmail = coachProfile.getUser().getEmail();

        if (coachEmail == null || coachEmail.isBlank()) {
            log.warn("Skipping creation email: coach email is missing, request id={}", request.getId());
            return;
        }

        try {
            SimpleMailMessage message = createBaseMessage();
            message.setTo(coachEmail);
            message.setSubject("[Caloriex] New training request received");

            String replyTo = requester.getEmail();
            if (replyTo != null && !replyTo.isBlank()) {
                message.setReplyTo(replyTo.trim());
            }

            message.setText(templateBuilder.buildCreationMailBody(request, requester, coachProfile));

            mailSender.send(message);

        } catch (Exception ex) {
            log.warn("Failed to send training request creation email to coach '{}', request id={}",
                    coachEmail, request.getId(), ex);
        }
    }

    public void sendStatusUpdateEmail(TrainingRequest request) {
        String requesterEmail = request.getRequesterUser().getEmail();

        if (requesterEmail == null || requesterEmail.isBlank()) {
            log.warn("Skipping status update email: requester email is missing, request id={}", request.getId());
            return;
        }

        try {
            SimpleMailMessage message = createBaseMessage();
            message.setTo(requesterEmail);
            message.setSubject("[Caloriex] Your training request status was updated");
            message.setText(templateBuilder.buildStatusUpdateMailBody(request));

            mailSender.send(message);

        } catch (Exception ex) {
            log.warn("Failed to send training request status update email to requester '{}', request id={}",
                    requesterEmail, request.getId(), ex);
        }
    }

    private SimpleMailMessage createBaseMessage() {
        SimpleMailMessage message = new SimpleMailMessage();

        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress.trim());
        }

        return message;
    }
}
