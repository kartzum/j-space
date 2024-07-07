package io.rdlab.cons.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class TinyStatisticsTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(TinyStatisticsTask.class);

    private final AtomicLong requestsCounter;
    private final AtomicLong requestsPrevCounter = new AtomicLong();
    private final ConcurrentLinkedQueue<TinyStatistics> queue;
    private final boolean logging;

    public TinyStatisticsTask(
            AtomicLong requestsCounter,
            ConcurrentLinkedQueue<TinyStatistics> queue,
            boolean logging
    ) {
        this.requestsCounter = requestsCounter;
        this.queue = queue;
        this.logging = logging;
    }

    @Override
    public void run() {
        long prev = requestsPrevCounter.get();
        long current = requestsCounter.get();
        requestsPrevCounter.updateAndGet(l -> current);
        Instant instant = Instant.now();
        long requestsDif = current - prev;
        queue.add(new TinyStatistics(instant, requestsDif));
        if (logging) {
            LOG.info("Requests: requests: {}, dif: {}.", current, requestsDif);
        }
    }

    public record TinyStatistics(Instant instant, long requestsDif) {
    }
}
