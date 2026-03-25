package com.example.caloriexbackend.trainingrequest.trainingplan.service;

import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.storage.StorageService;
import com.example.caloriexbackend.storage.payload.ProtectedDocumentUploadResponse;
import com.example.caloriexbackend.storage.payload.StoredFileDownload;
import com.example.caloriexbackend.trainingrequest.trainingplan.mapper.TrainingPlanMapper;
import com.example.caloriexbackend.trainingrequest.trainingplan.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {

    private final StorageService storageService;
    private final TrainingPlanRepository repository;
    private final TrainingPlanMapper trainingPlanMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public TrainingPlan create(
            TrainingRequest request,
            MultipartFile file,
            String planName,
            String description
    ) {
        ProtectedDocumentUploadResponse upload = storageService.uploadTrainingPlan(file);

        TrainingPlan trainingPlan = trainingPlanMapper.toEntity(
                request,
                planName,
                description,
                upload
        );

        return repository.save(trainingPlan);
    }

    @Transactional(readOnly = true)
    public StoredFileDownload downloadByTrainingRequestId(java.util.UUID trainingRequestId) {
        TrainingPlan trainingPlan = repository.findByTrainingRequestId(trainingRequestId)
                .orElseThrow(() -> new NotFoundException("Training plan not found"));

        java.util.UUID currentUserId = authenticatedUserService.getUser().getId();
        boolean isCoach = trainingPlan.getCoachUser().getId().equals(currentUserId);
        boolean isRequester = trainingPlan.getRequesterUser().getId().equals(currentUserId);

        if (!isCoach && !isRequester) {
            throw new NotFoundException("Training plan not found");
        }

        return storageService.downloadTrainingPlan(
                trainingPlan.getStorageKey(),
                trainingPlan.getFileName(),
                trainingPlan.getContentType()
        );
    }
}
