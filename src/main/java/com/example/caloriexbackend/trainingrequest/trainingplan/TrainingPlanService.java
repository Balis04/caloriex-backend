package com.example.caloriexbackend.trainingrequest.trainingplan;

import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.storage.StorageService;
import com.example.caloriexbackend.storage.payload.DocumentUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {

    private final StorageService storageService;
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
