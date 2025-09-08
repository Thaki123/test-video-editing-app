package com.example.worker.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default implementation that checks ffmpeg for NVENC encoders.
 */
@Component
public class DefaultGpuChecker implements GpuChecker {

    private static final Logger log = LoggerFactory.getLogger(DefaultGpuChecker.class);

    @Override
    public boolean isNvencAvailable() {
        try {
            Process process = new ProcessBuilder("ffmpeg", "-hide_banner", "-encoders").start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return output.contains("nvenc");
        } catch (IOException e) {
            log.warn("Unable to check NVENC availability: {}", e.getMessage());
            return false;
        }
    }
}
