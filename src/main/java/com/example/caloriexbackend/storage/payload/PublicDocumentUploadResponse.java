package com.example.caloriexbackend.storage.payload;

public record PublicDocumentUploadResponse(
        String originalFileName,
        String fileUrl,
        String contentType,
        long size
) {
}
