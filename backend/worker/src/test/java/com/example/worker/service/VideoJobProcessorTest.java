package com.example.worker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.worker.job.VideoJob;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class VideoJobProcessorTest {

    static class TestPublisher implements ProgressPublisher {
        final List<Integer> progresses = new ArrayList<>();
        @Override public void publish(String jobId, int progress) { progresses.add(progress); }
    }

    @Test
    void publishesProgressForEachStage() {
        GpuChecker gpu = () -> true;
        TestPublisher publisher = new TestPublisher();
        VideoJobProcessor processor = new VideoJobProcessor(gpu, publisher);
        processor.process(new VideoJob("job", true));
        assertEquals(List.of(0,10,40,60,80,100), publisher.progresses);
    }

    @Test
    void warnsWhenNvencUnavailable() {
        GpuChecker gpu = () -> false;
        TestPublisher publisher = new TestPublisher();
        VideoJobProcessor processor = new VideoJobProcessor(gpu, publisher);

        Logger logger = (Logger) LoggerFactory.getLogger(VideoJobProcessor.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        processor.process(new VideoJob("job", false));

        boolean warned = appender.list.stream()
            .anyMatch(e -> e.getLevel() == Level.WARN && e.getFormattedMessage().contains("NVENC not detected"));
        assertTrue(warned, "Expected NVENC fallback warning");
    }
}
