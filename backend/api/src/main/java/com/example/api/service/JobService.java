package com.example.api.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.api.web.JobRequest;
import com.example.api.web.JobStatusResponse;

@Service
public class JobService {
    private record JobData(String id, String status, String downloadKey) {}

    private final RabbitTemplate rabbitTemplate;
    private final StorageService storageService;
    private final Map<String, JobData> store = new ConcurrentHashMap<>();

    public JobService(RabbitTemplate rabbitTemplate, StorageService storageService) {
        this.rabbitTemplate = rabbitTemplate;
        this.storageService = storageService;
    }

    public String enqueueJob(JobRequest request) {
        String id = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend("jobs", id);
        store.put(id, new JobData(id, "QUEUED", null));
        return id;
    }

    public JobStatusResponse getJob(String id) {
        JobData data = store.get(id);
        if (data == null) {
            return new JobStatusResponse(id, "NOT_FOUND", null);
        }
        String url = data.downloadKey != null ? storageService.generatePresignedDownloadUrl(data.downloadKey) : null;
        return new JobStatusResponse(data.id, data.status, url);
    }

    public void completeJob(String id, String objectKey) {
        store.put(id, new JobData(id, "COMPLETED", objectKey));
    }
}
