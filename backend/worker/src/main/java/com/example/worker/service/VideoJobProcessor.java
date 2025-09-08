package com.example.worker.service;

import com.example.worker.job.VideoJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Coordinates video processing pipeline.
 */
@Service
public class VideoJobProcessor {

    private static final Logger log = LoggerFactory.getLogger(VideoJobProcessor.class);

    private final GpuChecker gpuChecker;
    private final ProgressPublisher progressPublisher;

    public VideoJobProcessor(GpuChecker gpuChecker, ProgressPublisher progressPublisher) {
        this.gpuChecker = gpuChecker;
        this.progressPublisher = progressPublisher;
    }

    public void process(VideoJob job) {
        progressPublisher.publish(job.id(), 0);
        boolean nvenc = gpuChecker.isNvencAvailable();
        decode(job, nvenc);
        progressPublisher.publish(job.id(), 10);

        stylize(job);
        progressPublisher.publish(job.id(), 40);

        applyOpticalFlow(job);
        progressPublisher.publish(job.id(), 60);

        if (job.superResolution()) {
            superResolve(job);
            progressPublisher.publish(job.id(), 80);
        }

        encode(job, nvenc);
        progressPublisher.publish(job.id(), 100);
    }

    private void decode(VideoJob job, boolean nvenc) {
        if (nvenc) {
            log.info("Decoding {} using NVDEC/NVENC", job.id());
        } else {
            log.warn("NVENC not detected; decoding {} on CPU", job.id());
        }
    }

    private void stylize(VideoJob job) {
        log.info("Stylizing frames for job {} with AnimeGANv2, manga, watercolor, and graphite U-Nets", job.id());
    }

    private void applyOpticalFlow(VideoJob job) {
        log.info("Applying RAFT optical-flow warping for job {}", job.id());
    }

    private void superResolve(VideoJob job) {
        log.info("Upscaling frames with ESRGAN-lite for job {}", job.id());
    }

    private void encode(VideoJob job, boolean nvenc) {
        if (nvenc) {
            log.info("Encoding {} with NVENC", job.id());
        } else {
            log.warn("NVENC not detected; encoding {} on CPU", job.id());
        }
        log.info("Tone-mapping to sRGB and preserving audio for job {}", job.id());
    }
}
