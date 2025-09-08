package com.example.api.service;

public interface StorageService {
    String generatePresignedUrl(String objectKey);

    String generatePresignedDownloadUrl(String objectKey);
}
