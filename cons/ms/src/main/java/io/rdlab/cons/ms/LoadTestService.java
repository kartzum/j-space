package io.rdlab.cons.ms;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.io.Closeable;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

public class LoadTestService implements Runnable, Closeable {
    private final long tokensCapacity;
    private final long iterations;
    private final Duration period;
    private final TaskGenerator taskGenerator;

    private boolean running;
    private final ExecutorService processingExecutorService;

    public LoadTestService(
            long tokensCapacity,
            long iterations,
            Duration period,
            TaskGenerator taskGenerator
    ) {
        this.tokensCapacity = tokensCapacity;
        this.iterations = iterations;
        this.period = period;
        this.taskGenerator = taskGenerator;
        this.processingExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        if (running) {
            return;
        }
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(tokensCapacity).refillIntervally(tokensCapacity, period)
                .build();
        Bucket bucket = Bucket.builder()
                .addLimit(bandwidth)
                .build();
        running = true;
        processingExecutorService.submit(new Processor(bucket));
    }

    @Override
    public void close() {
        if (!running) {
            return;
        }
        running = false;
    }

    private class Processor implements Runnable {
        private final Bucket bucket;

        public Processor(Bucket bucket) {
            this.bucket = bucket;
        }

        @Override
        public void run() {
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                LongStream.range(0, iterations).forEach((i) -> {
                    bucket.asBlocking().consumeUninterruptibly(1);
                    if (!running) {
                        return;
                    }
                    Task task = taskGenerator.generate();
                    executor.submit(task);
                });
            }
        }
    }

    public interface Task extends Runnable {
    }

    public interface TaskGenerator {
        Task generate();
    }
}
