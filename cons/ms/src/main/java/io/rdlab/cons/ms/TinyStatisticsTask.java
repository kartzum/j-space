package io.rdlab.cons.ms;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class TinyStatisticsTask implements Runnable {

    private final AtomicLong requestsCounter;
    private final AtomicLong requestsPrevCounter = new AtomicLong();
    private final ConcurrentLinkedQueue<TinyStatistics> queue;

    public TinyStatisticsTask(
            AtomicLong requestsCounter,
            ConcurrentLinkedQueue<TinyStatistics> queue
    ) {
        this.requestsCounter = requestsCounter;
        this.queue = queue;
    }

    @Override
    public void run() {
        long prev = requestsPrevCounter.get();
        long current = requestsCounter.get();
        requestsPrevCounter.updateAndGet(l -> current);
        queue.add(new TinyStatistics(Instant.now(), current - prev));
    }

    public record TinyStatistics(Instant instant, long requestsDif) {
    }
}
