package com.example.caloriexbackend.trainingrequest.email;

import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingRequestEmailTemplateBuilder {

    @Value("${app.frontend-base-url:${app.auth.frontend-base-url:http://localhost:5173}}")
    private String frontendBaseUrl;

    public Map<String, Object> buildCreationVariables(TrainingRequest trainingRequest, User requester, CoachProfile coachProfile) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("subject", "New training request submitted");
        vars.put("preview", "A new training request has been submitted in Caloriex.");
        vars.put("headline", "New Training Request");
        vars.put("intro_text", "A new training request has been submitted in the Caloriex system.");
        vars.put("status", EmailFormatUtils.status(trainingRequest.getStatus()));
        vars.put("coach_name", EmailFormatUtils.safe(coachProfile.getUser().getFullName()));
        vars.put("requester_name", EmailFormatUtils.safe(requester.getFullName()));
        vars.put("requester_email", EmailFormatUtils.safe(requester.getEmail()));
        vars.put("current_weight", EmailFormatUtils.weight(requester.getActualWeightKg()));
        vars.put("target_weight", EmailFormatUtils.weight(requester.getTargetWeightKg()));
        vars.put("goal", EmailFormatUtils.goal(requester.getGoal()));
        vars.put("activity_level", EmailFormatUtils.activityLevel(requester.getActivityLevel()));
        vars.put("weekly_training_count", String.valueOf(trainingRequest.getWeeklyTrainingCount()));
        vars.put("session_duration_minutes", String.valueOf(trainingRequest.getSessionDurationMinutes()));
        vars.put("preferred_location", EmailFormatUtils.safe(trainingRequest.getPreferredLocation()));
        vars.put("request_description", EmailFormatUtils.safe(trainingRequest.getRequestDescription()));
        vars.put("request_id", String.valueOf(trainingRequest.getId()));
        vars.put("created_at", String.valueOf(trainingRequest.getCreatedAt()));
        vars.put("app_url", frontendBaseUrl + "/training-requests/" + trainingRequest.getId());
        return vars;
    }

    public Map<String, Object> buildStatusUpdateVariables(TrainingRequest trainingRequest) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("subject", "Training request status updated");
        vars.put("preview", "Your training request status has been updated.");
        vars.put("headline", "Training Request Status Updated");
        vars.put("intro_text", "The status of your training request has been updated in the Caloriex system.");
        vars.put("status", EmailFormatUtils.status(trainingRequest.getStatus()));
        vars.put("coach_name", EmailFormatUtils.safe(trainingRequest.getCoachProfile().getUser().getFullName()));
        vars.put("requester_name", EmailFormatUtils.safe(trainingRequest.getRequesterUser().getFullName()));
        vars.put("requester_email", EmailFormatUtils.safe(trainingRequest.getRequesterUser().getEmail()));
        vars.put("current_weight", EmailFormatUtils.weight(trainingRequest.getRequesterUser().getActualWeightKg()));
        vars.put("target_weight", EmailFormatUtils.weight(trainingRequest.getRequesterUser().getTargetWeightKg()));
        vars.put("goal", EmailFormatUtils.goal(trainingRequest.getRequesterUser().getGoal()));
        vars.put("activity_level", EmailFormatUtils.activityLevel(trainingRequest.getRequesterUser().getActivityLevel()));
        vars.put("weekly_training_count", String.valueOf(trainingRequest.getWeeklyTrainingCount()));
        vars.put("session_duration_minutes", String.valueOf(trainingRequest.getSessionDurationMinutes()));
        vars.put("preferred_location", EmailFormatUtils.safe(trainingRequest.getPreferredLocation()));
        vars.put("request_description", EmailFormatUtils.safe(trainingRequest.getRequestDescription()));
        vars.put("coach_response", EmailFormatUtils.safe(trainingRequest.getCoachResponse()));
        vars.put("coach_note", EmailFormatUtils.safe(trainingRequest.getCoachResponse()));
        vars.put("request_id", String.valueOf(trainingRequest.getId()));
        vars.put("created_at", String.valueOf(trainingRequest.getCreatedAt()));
        vars.put("app_url", frontendBaseUrl + "/training-requests/" + trainingRequest.getId());
        return vars;
    }

    public Map<String, Object> buildTrainingPlanUploadedVariables(TrainingRequest trainingRequest, TrainingPlan trainingPlan) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("subject", "Your training plan is ready");
        vars.put("preview", "Your training plan has been uploaded to Caloriex.");
        vars.put("headline", "Your Training Plan Is Ready");
        vars.put("intro_text", "Your training plan has been uploaded to the Caloriex app.");
        vars.put("coach_name", EmailFormatUtils.safe(trainingRequest.getCoachProfile().getUser().getFullName()));
        vars.put("plan_name", EmailFormatUtils.safe(trainingPlan.getPlanName()));
        vars.put("plan_description", EmailFormatUtils.safe(trainingPlan.getPlanDescription()));
        vars.put("file_name", EmailFormatUtils.safe(trainingPlan.getFileName()));
        vars.put("uploaded_at", String.valueOf(trainingPlan.getUploadedAt()));
        vars.put("request_id", String.valueOf(trainingRequest.getId()));
        vars.put("app_url", frontendBaseUrl + "/training-requests/" + trainingRequest.getId());
        return vars;
    }
}
