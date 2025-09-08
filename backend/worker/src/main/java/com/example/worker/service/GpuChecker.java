package com.example.worker.service;

/**
 * Detects GPU capabilities such as NVENC support.
 */
public interface GpuChecker {
    /**
     * @return true if NVENC encoder is available
     */
    boolean isNvencAvailable();
}
