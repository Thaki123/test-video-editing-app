package com.example.api.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
public class S3StorageService implements StorageService {
    private final S3Presigner presigner;

    public S3StorageService(S3Presigner presigner) {
        this.presigner = presigner;
    }

    @Override
    public String generatePresignedUrl(String objectKey) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket("uploads")
                .key(objectKey)
                .build();
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest));
        return presignedRequest.url().toString();
    }

    @Override
    public String generatePresignedDownloadUrl(String objectKey) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket("uploads")
                .key(objectKey)
                .build();
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(r -> r
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(objectRequest));
        return presignedRequest.url().toString();
    }
}
