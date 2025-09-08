package com.example.worker.job;

/**
 * Job description for video processing.
 */
public record VideoJob(String id, boolean superResolution) {
}
