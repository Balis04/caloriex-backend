package com.example.caloryxbackend.trainingrequest.trainingplan;

import com.example.caloryxbackend.entities.TrainingPlan;
import com.example.caloryxbackend.entities.TrainingRequest;
import com.example.caloryxbackend.storage.CertificateStorageService;
import com.example.caloryxbackend.storage.payload.DocumentUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {

    private final CertificateStorageService storageService;
    private final TrainingPlanRepository repository;
    private final TrainingPlanMapper trainingPlanMapper;

    public TrainingPlan create(
            TrainingRequest request,
            MultipartFile file,
            String planName,
            String description
    ) {
        DocumentUploadResponse upload = storageService.uploadTrainingPlan(file);

        TrainingPlan trainingPlan = trainingPlanMapper.toEntity(
                request,
                planName,
                description,
                upload
        );

        return repository.save(trainingPlan);
    }
}
