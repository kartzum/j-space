package io.rdlab.cons.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import static io.rdlab.cons.ms.Utils.round;

public class TinyStatisticsTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(TinyStatisticsTask.class);

    private final AtomicLong requestsCounter;
    private final AtomicLong requestsPrevCounter = new AtomicLong();
    private final ConcurrentLinkedQueue<TinyStatistics> queue;
    private final boolean logging;

    private final TinyStatisticsDumpService tinyStatisticsDumpService;

    public TinyStatisticsTask(
            AtomicLong requestsCounter,
            ConcurrentLinkedQueue<TinyStatistics> queue,
            boolean logging,
            TinyStatisticsDumpService tinyStatisticsDumpService
    ) {
        this.requestsCounter = requestsCounter;
        this.queue = queue;
        this.logging = logging;
        this.tinyStatisticsDumpService = tinyStatisticsDumpService;
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
            TinyStatisticsDumpService.TinyStatisticsDump tinyStatisticsDump =
                    tinyStatisticsDumpService.generateDump();
            LOG.info(
                    "Rs: {}, ers: {}, avg (~rps): {}, avg time (ms): {}, ds: {}, of: {}" +
                            ", dif: {}.",
                    tinyStatisticsDump.requests(),
                    tinyStatisticsDump.requestsErrors(),
                    round(tinyStatisticsDump.avgRequestsDif()),
                    round(tinyStatisticsDump.avgRequestsTimeElapsed()),
                    tinyStatisticsDump.requestsCountDowns(),
                    tinyStatisticsDump.openFileDescriptorCount(),
                    requestsDif
            );
        }
    }

    public record TinyStatistics(Instant instant, long requestsDif) {
    }
}
