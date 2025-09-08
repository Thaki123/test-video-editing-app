package com.example.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.example.api.web.JobRequest;
import com.example.api.web.JobStatusResponse;

class JobServiceTest {

    @Test
    void enqueueStoresJobAndPublishesToQueue() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        StorageService storage = mock(StorageService.class);
        JobService service = new JobService(rabbit, storage);

        String id = service.enqueueJob(new JobRequest("key", "style"));
        assertNotNull(id);
        verify(rabbit).convertAndSend("jobs", id);

        JobStatusResponse status = service.getJob(id);
        assertEquals("QUEUED", status.status());
        assertEquals(id, status.id());
    }

    @Test
    void completeJobUpdatesStatusAndReturnsUrl() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        StorageService storage = mock(StorageService.class);
        when(storage.generatePresignedDownloadUrl("obj")).thenReturn("url");
        JobService service = new JobService(rabbit, storage);

        String id = service.enqueueJob(new JobRequest("k", "s"));
        service.completeJob(id, "obj");

        JobStatusResponse status = service.getJob(id);
        assertEquals("COMPLETED", status.status());
        assertEquals("url", status.downloadUrl());
    }

    @Test
    void missingJobReturnsNotFound() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        StorageService storage = mock(StorageService.class);
        JobService service = new JobService(rabbit, storage);

        JobStatusResponse status = service.getJob("missing");
        assertEquals("NOT_FOUND", status.status());
    }
}
