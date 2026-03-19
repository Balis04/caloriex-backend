package com.example.caloryxbackend.common.email;

import com.example.caloryxbackend.entities.CoachProfile;
import com.example.caloryxbackend.entities.TrainingRequest;
import com.example.caloryxbackend.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
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

        SimpleMailMessage message = createBaseMessage();
        message.setTo(coachEmail);
        message.setSubject("[Caloryx] Új edzésterv kérés érkezett");

        String replyTo = requester.getEmail();
        if (replyTo != null && !replyTo.isBlank()) {
            message.setReplyTo(replyTo.trim());
        }

        message.setText(templateBuilder.buildCreationMailBody(request, requester, coachProfile));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("Failed to send training request creation email to coach '{}', request id={}",
                    coachEmail, request.getId(), ex);
        }
    }

    public void sendStatusUpdateEmail(TrainingRequest request) {
        String requesterEmail = request.getRequesterUser().getEmail();

        SimpleMailMessage message = createBaseMessage();
        message.setTo(requesterEmail);
        message.setSubject("[Caloryx] Frissült az edzéskérelmed státusza");
        message.setText(templateBuilder.buildStatusUpdateMailBody(request));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("Failed to send training request status update email to requester '{}', request id={}",
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