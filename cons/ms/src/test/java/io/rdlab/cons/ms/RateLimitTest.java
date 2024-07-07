package io.rdlab.cons.ms;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RateLimitTest {
    private static final Logger LOG = LoggerFactory.getLogger(RateLimitTest.class);

    @Test
    void requestPerSecondTest() {
        long tokens = 10;
        long iterations = 30;
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(tokens).refillIntervally(tokens, Duration.ofSeconds(1))
                .build();
        Bucket bucket = Bucket.builder()
                .addLimit(bandwidth)
                .build();
        AtomicLong requestsCounter = new AtomicLong();
        StatisticsTask statisticsTask = new StatisticsTask(requestsCounter);
        Timer statisticsTimer = new Timer("Timer");
        statisticsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                statisticsTask.run();
            }
        }, 10L, 1000L);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            LongStream.range(0, iterations).forEach((i) -> {
                bucket.asBlocking().consumeUninterruptibly(1);
                executor.submit(() -> new Task(requestsCounter).run());
            });
        }
        LOG.info("Requests calls: {}.", requestsCounter.get());
        statisticsTask.run();
        assertEquals(iterations, requestsCounter.get());
    }

    private static class Task implements Runnable {
        private final AtomicLong requestsCounter;

        public Task(
                AtomicLong requestsCounter
        ) {
            this.requestsCounter = requestsCounter;
        }

        public void run() {
            requestsCounter.incrementAndGet();
        }
    }

    private static class StatisticsTask implements Runnable {
        private final AtomicLong requestsCounter;
        private final AtomicLong requestsPrevCounter = new AtomicLong();

        public StatisticsTask(AtomicLong requestsCounter) {
            this.requestsCounter = requestsCounter;
        }

        @Override
        public void run() {
            long prev = requestsPrevCounter.get();
            long current = requestsCounter.get();
            requestsPrevCounter.updateAndGet(l -> current);
            LOG.info("Statistics: {}, {}, {}.", current, prev, current - prev);
        }
    }
}
