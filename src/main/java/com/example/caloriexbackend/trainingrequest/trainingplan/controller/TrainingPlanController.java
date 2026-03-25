package com.example.caloriexbackend.trainingrequest.trainingplan.controller;

import com.example.caloriexbackend.storage.payload.StoredFileDownload;
import com.example.caloriexbackend.trainingrequest.trainingplan.service.TrainingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/training-requests")
@RequiredArgsConstructor
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;

    @GetMapping("/{trainingRequestId}/training-plan/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable UUID trainingRequestId) {
        StoredFileDownload file = trainingPlanService.downloadByTrainingRequestId(trainingRequestId);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (file.contentType() != null && !file.contentType().isBlank()) {
            mediaType = MediaType.parseMediaType(file.contentType());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(file.content().length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(file.fileName()).build().toString()
                )
                .body(new ByteArrayResource(file.content()));
    }
}
