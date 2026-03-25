package com.example.caloriexbackend.storage.payload;

public record StoredFileDownload(
        String fileName,
        String contentType,
        byte[] content
) {
}
