package com.example.worker.service;

/**
 * Publishes progress updates for a job.
 */
public interface ProgressPublisher {
    void publish(String jobId, int progress);
}
