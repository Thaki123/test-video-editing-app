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
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, JobStatusResponse> store = new ConcurrentHashMap<>();

    public JobService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String enqueueJob(JobRequest request) {
        String id = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend("jobs", id);
        store.put(id, new JobStatusResponse(id, "QUEUED", null));
        return id;
    }

    public JobStatusResponse getJob(String id) {
        return store.getOrDefault(id, new JobStatusResponse(id, "NOT_FOUND", null));
    }
}
