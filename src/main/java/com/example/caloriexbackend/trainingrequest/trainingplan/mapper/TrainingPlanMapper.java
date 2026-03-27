package com.example.caloriexbackend.trainingrequest.trainingplan.mapper;

import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.storage.payload.ProtectedDocumentUploadResponse;
import com.example.caloriexbackend.trainingrequest.trainingplan.payload.TrainingPlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingPlanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingRequest", source = "request")
    @Mapping(target = "planName", source = "planName")
    @Mapping(target = "planDescription", source = "planDescription")
    @Mapping(target = "fileName", source = "upload.originalFileName")
    @Mapping(target = "storageKey", source = "upload.storageKey")
    @Mapping(target = "contentType", source = "upload.contentType")
    @Mapping(target = "fileSizeBytes", source = "upload.size")
    @Mapping(target = "uploadedAt", ignore = true)
    TrainingPlan toEntity(
            TrainingRequest request,
            String planName,
            String planDescription,
            ProtectedDocumentUploadResponse upload
    );

    TrainingPlanResponse toResponse(TrainingPlan entity);


}
