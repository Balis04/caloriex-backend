package com.example.caloriexbackend.storage.payload;

public record DocumentUploadResponse(
        String originalFileName,
        String storageKey,
        String fileUrl,
        String contentType,
        long size
) {}
