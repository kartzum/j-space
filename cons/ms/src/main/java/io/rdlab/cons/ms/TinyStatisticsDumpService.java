package io.rdlab.cons.ms;

import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static io.rdlab.cons.ms.Utils.round;

public class TinyStatisticsDumpService {
    private static final Logger LOG = LoggerFactory.getLogger(TinyStatisticsDumpService.class);

    private final boolean logging;
    private final AtomicLong requestsCounter;
    private final ConcurrentLinkedQueue<TinyStatisticsTask.TinyStatistics> tinyStatisticsQueue;
    private final AtomicLong requestsErrorsCounter;
    private final AtomicLong requestsTimeElapsedCounter;
    private final CountDownLatch requestsCountDownLatch;
    private final UnixOperatingSystemMXBean osMBean;

    public TinyStatisticsDumpService(
            boolean logging,
            AtomicLong requestsCounter,
            ConcurrentLinkedQueue<TinyStatisticsTask.TinyStatistics> tinyStatisticsQueue,
            AtomicLong requestsErrorsCounter,
            AtomicLong requestsTimeElapsedCounter,
            CountDownLatch requestsCountDownLatch
    ) {
        this.logging = logging;
        this.requestsCounter = requestsCounter;
        this.tinyStatisticsQueue = tinyStatisticsQueue;
        this.requestsErrorsCounter = requestsErrorsCounter;
        this.requestsTimeElapsedCounter = requestsTimeElapsedCounter;
        this.requestsCountDownLatch = requestsCountDownLatch;
        this.osMBean = (UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public TinyStatisticsDump generateDump() {
        AtomicLong totalRequestsDiffs = new AtomicLong();
        AtomicLong totalRequests = new AtomicLong();
        tinyStatisticsQueue.forEach(statistics -> {
            if (statistics.requestsDif() > 0) {
                totalRequestsDiffs.addAndGet(statistics.requestsDif());
                totalRequests.incrementAndGet();
            }
        });
        double avgRequestsDif = (double) totalRequestsDiffs.get() / totalRequests.get();
        double avgRequestsTimeElapsed = (double) requestsTimeElapsedCounter.get() / requestsCounter.get();
        return new TinyStatisticsDump(
                requestsCounter.get(),
                requestsErrorsCounter.get(),
                avgRequestsDif,
                avgRequestsTimeElapsed,
                requestsCountDownLatch.getCount(),
                osMBean.getOpenFileDescriptorCount(),
                osMBean.getMaxFileDescriptorCount()
        );
    }

    public void printStatistics() {
        if (!logging) {
            return;
        }
        TinyStatisticsDump tinyStatisticsDump = generateDump();
        LOG.info(
                "Rs: {}, ers: {}, avg (~rps): {}, avg time (ms): {}, ds: {}, of: {}.",
                tinyStatisticsDump.requests(),
                tinyStatisticsDump.requestsErrors(),
                round(tinyStatisticsDump.avgRequestsDif()),
                round(tinyStatisticsDump.avgRequestsTimeElapsed()),
                tinyStatisticsDump.openFileDescriptorCount(),
                tinyStatisticsDump.requestsCountDowns()
        );
    }

    public record TinyStatisticsDump(
            long requests,
            long requestsErrors,
            double avgRequestsDif,
            double avgRequestsTimeElapsed,
            long requestsCountDowns,
            long openFileDescriptorCount,
            long maxFileDescriptorCount
    ) {
    }
}
