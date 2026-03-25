package com.example.caloriexbackend.trainingrequest.mapper;

import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.trainingrequest.payload.request.TrainingRequestCreateRequest;
import com.example.caloriexbackend.trainingrequest.payload.response.ClosedTrainingRequestResponse;
import com.example.caloriexbackend.trainingrequest.payload.response.TrainingRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requesterUser", source = "requester")
    @Mapping(target = "coachProfile", source = "coachProfile")
    @Mapping(target = "weeklyTrainingCount", source = "request.weeklyTrainingCount")
    @Mapping(target = "sessionDurationMinutes", source = "request.sessionDurationMinutes")
    @Mapping(target = "preferredLocation", source = "request.preferredLocation")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "coachNote", source = "request.coachNote")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TrainingRequest toEntity(
            TrainingRequestCreateRequest request,
            TrainingRequestStatus status,
            User requester,
            CoachProfile coachProfile
    );

    @Mapping(source = "coachProfile.id", target = "coachProfileId")
    @Mapping(source = "requesterUser.id", target = "requesterUserId")
    @Mapping(source = "coachProfile.user.fullName", target = "coachName")
    @Mapping(source = "requesterUser.fullName", target = "requesterName")
    @Mapping(source = "requesterUser.email", target = "requesterEmail")
    TrainingRequestResponse toResponse(TrainingRequest entity);

    @Mapping(source = "trainingRequest.id", target = "requestId")
    @Mapping(source = "trainingRequest.coachProfile.user.fullName", target = "coachName")
    @Mapping(source = "trainingRequest.requesterUser.fullName", target = "requesterName")
    @Mapping(source = "trainingRequest.requesterUser.email", target = "requesterEmail")
    @Mapping(source = "trainingRequest.weeklyTrainingCount", target = "weeklyTrainingCount")
    @Mapping(source = "trainingRequest.sessionDurationMinutes", target = "sessionDurationMinutes")
    @Mapping(source = "trainingRequest.preferredLocation", target = "preferredLocation")
    @Mapping(source = "trainingRequest.status", target = "status")
    @Mapping(source = "trainingRequest.description", target = "description")
    @Mapping(source = "trainingRequest.coachNote", target = "coachNote")
    @Mapping(source = "trainingRequest.createdAt", target = "createdAt")

    @Mapping(source = "planName", target = "planName")
    @Mapping(source = "description", target = "planDescription")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "uploadedAt", target = "uploadedAt")
    ClosedTrainingRequestResponse toClosedResponse(TrainingPlan entity);
}
