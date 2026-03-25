package com.example.caloriexbackend.storage.payload;

public record ProtectedDocumentUploadResponse(
        String originalFileName,
        String storageKey,
        String contentType,
        long size
) {
}
