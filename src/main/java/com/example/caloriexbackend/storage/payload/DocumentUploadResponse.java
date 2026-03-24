package com.example.caloriexbackend.storage.payload;

public record DocumentUploadResponse(
        String originalFileName,
        String fileUrl,
        String contentType,
        long size
) {}
