package com.example.caloriexbackend.trainingrequest.email;

import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import org.springframework.stereotype.Component;

@Component
public class TrainingRequestEmailTemplateBuilder {

    public String buildCreationMailBody(TrainingRequest trainingRequest, User requester, CoachProfile coachProfile) {
        return """
        A new training request has been submitted in the Caloriex system.

        Coach: %s

        Requester details:
        - Name: %s
        - Email: %s
        - Current weight: %s
        - Target weight: %s
        - Goal: %s
        - Activity level: %s

        Training preferences:
        - Weekly training sessions: %s
        - Session duration: %s minutes
        - Preferred location: %s
        - Request description: %s

        Request status: %s
        Request ID: %s
        Created at: %s

        Please log in to the application for more details.
        """.formatted(
                EmailFormatUtils.safe(coachProfile.getUser().getFullName()),
                EmailFormatUtils.safe(requester.getFullName()),
                EmailFormatUtils.safe(requester.getEmail()),
                EmailFormatUtils.weight(requester.getActualWeightKg()),
                EmailFormatUtils.weight(requester.getTargetWeightKg()),
                EmailFormatUtils.goal(requester.getGoal()),
                EmailFormatUtils.activityLevel(requester.getActivityLevel()),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                EmailFormatUtils.safe(trainingRequest.getPreferredLocation()),
                EmailFormatUtils.safe(trainingRequest.getRequestDescription()),
                EmailFormatUtils.status(trainingRequest.getStatus()),
                trainingRequest.getId(),
                trainingRequest.getCreatedAt()
        );
    }

    public String buildStatusUpdateMailBody(TrainingRequest trainingRequest) {
        return """
        The status of your training request has been updated in the Caloriex system.

        New status: %s
        Coach: %s
        Weekly training sessions: %s
        Session duration: %s minutes
        Preferred location: %s
        Request description: %s
        Coach response: %s
        Request ID: %s
        Created at: %s

        Please log in to the application for more details.
        """.formatted(
                EmailFormatUtils.status(trainingRequest.getStatus()),
                EmailFormatUtils.safe(trainingRequest.getCoachProfile().getUser().getFullName()),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                EmailFormatUtils.safe(trainingRequest.getPreferredLocation()),
                EmailFormatUtils.safe(trainingRequest.getRequestDescription()),
                EmailFormatUtils.safe(trainingRequest.getCoachResponse()),
                trainingRequest.getId(),
                trainingRequest.getCreatedAt()
        );
    }
}
