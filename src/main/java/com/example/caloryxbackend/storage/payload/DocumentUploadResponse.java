package com.example.caloryxbackend.storage.payload;

public record DocumentUploadResponse(
        String originalFileName,
        String fileUrl,
        String contentType,
        long size
) {}
