package com.example.caloryxbackend.trainingrequest.trainingplan;

import com.example.caloryxbackend.entities.TrainingPlan;
import com.example.caloryxbackend.entities.TrainingRequest;
import com.example.caloryxbackend.storage.payload.DocumentUploadResponse;
import com.example.caloryxbackend.trainingrequest.payload.response.TrainingPlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingPlanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingRequest", source = "request")
    @Mapping(target = "coachUser", source = "request.coachProfile.user")
    @Mapping(target = "requesterUser", source = "request.requesterUser")
    @Mapping(target = "planName", source = "planName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "fileName", source = "upload.originalFileName")
    @Mapping(target = "fileUrl", source = "upload.fileUrl")
    @Mapping(target = "contentType", source = "upload.contentType")
    @Mapping(target = "fileSizeBytes", source = "upload.size")
    TrainingPlan toEntity(
            TrainingRequest request,
            String planName,
            String description,
            DocumentUploadResponse upload
    );

    TrainingPlanResponse toResponse(TrainingPlan entity);


}
