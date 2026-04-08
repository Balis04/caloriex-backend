package com.example.caloriexbackend.communitytrainingplan.service;

import com.example.caloriexbackend.communitytrainingplan.payload.CommunityTrainingPlanResponse;
import com.example.caloriexbackend.storage.StorageService;
import com.example.caloriexbackend.storage.payload.StoredFileDownload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityTrainingPlanService {

    private final StorageService storageService;

    public List<CommunityTrainingPlanResponse> getAll() {
        return storageService.listCommunityTrainingPlans().stream()
                .map(plan -> new CommunityTrainingPlanResponse(
                        plan.fileName(),
                        plan.size(),
                        plan.lastModified(),
                        "/api/community-training-plans/%s/download".formatted(plan.fileName())
                ))
                .toList();
    }

    public StoredFileDownload download(String fileName) {
        return storageService.downloadCommunityTrainingPlan(fileName);
    }
}
