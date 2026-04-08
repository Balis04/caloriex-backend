package com.example.caloriexbackend.communitytrainingplan.service;

import com.example.caloriexbackend.communitytrainingplan.payload.CommunityTrainingPlanResponse;
import com.example.caloriexbackend.storage.StorageService;
import com.example.caloriexbackend.storage.payload.StoredFileDownload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityTrainingPlanServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private CommunityTrainingPlanService communityTrainingPlanService;

    @Test
    void getAllShouldMapStorageSummariesToResponses() {
        StorageService.CommunityTrainingPlanSummary firstPlan = new StorageService.CommunityTrainingPlanSummary(
                "beginner-full-body.pdf",
                245760L,
                Instant.parse("2026-03-30T17:10:00Z")
        );
        StorageService.CommunityTrainingPlanSummary secondPlan = new StorageService.CommunityTrainingPlanSummary(
                "upper-lower-split.docx",
                512000L,
                Instant.parse("2026-04-01T08:30:00Z")
        );

        when(storageService.listCommunityTrainingPlans()).thenReturn(List.of(firstPlan, secondPlan));

        List<CommunityTrainingPlanResponse> actual = communityTrainingPlanService.getAll();

        assertEquals(2, actual.size());
        assertEquals("beginner-full-body.pdf", actual.get(0).fileName());
        assertEquals(245760L, actual.get(0).size());
        assertEquals(Instant.parse("2026-03-30T17:10:00Z"), actual.get(0).lastModified());
        assertEquals("/api/community-training-plans/beginner-full-body.pdf/download", actual.get(0).downloadUrl());

        assertEquals("upper-lower-split.docx", actual.get(1).fileName());
        assertEquals(512000L, actual.get(1).size());
        assertEquals(Instant.parse("2026-04-01T08:30:00Z"), actual.get(1).lastModified());
        assertEquals("/api/community-training-plans/upper-lower-split.docx/download", actual.get(1).downloadUrl());
    }

    @Test
    void downloadShouldDelegateToStorageService() {
        String fileName = "beginner-full-body.pdf";
        StoredFileDownload download = new StoredFileDownload(
                fileName,
                "application/pdf",
                new byte[]{1, 2, 3}
        );

        when(storageService.downloadCommunityTrainingPlan(fileName)).thenReturn(download);

        StoredFileDownload actual = communityTrainingPlanService.download(fileName);

        assertSame(download, actual);
        verify(storageService).downloadCommunityTrainingPlan(fileName);
    }
}
