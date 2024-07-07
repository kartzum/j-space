package io.rdlab.cons.ms;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static io.rdlab.cons.ms.TestUtils.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TinyServerTest {
    private final Random random = new Random();

    @Test
    void receiveData() {
        int port = generatePort();
        TinyServer tinyServer = TinyServer.create("0.0.0.0", port, 1, new MultiplierHandler());
        tinyServer.run();
        TinyClient tinyClient = TinyClient.create("localhost", port, true);
        TinyClient.Response response = tinyClient.exchange(new byte[]{2});
        assertTrue(response.length() > 0);
        assertEquals(4, response.data()[0]);
        sleep(10);
        tinyServer.close();
        tinyClient.close();
    }

    @Test
    void simpleLoadTest() throws InterruptedException {
        long iterations = 31L;
        long tokensCapacity = 10L;
        boolean logging = false;
        boolean loggingStatistics = true;
        int port = generatePort();
        TinyServer tinyServer = TinyServer.create(
                "0.0.0.0",
                port,
                1,
                new MultiplierHandler()
        );
        CountDownLatch countDownLatch = new CountDownLatch((int) iterations);
        AtomicLong requestsCounter = new AtomicLong();
        AtomicLong requestsErrorsCounter = new AtomicLong();
        AtomicLong requestsTimeElapsedCounter = new AtomicLong();
        LoadTestService loadTestService = new LoadTestService(
                tokensCapacity,
                iterations,
                Duration.ofSeconds(1),
                new RandomTaskGenerator("localhost", port, logging, new RandomTaskGenerator.Handler() {
                    @Override
                    public void doBefore() {
                        requestsCounter.incrementAndGet();
                        countDownLatch.countDown();
                    }

                    @Override
                    public void doAfter() {
                    }

                    @Override
                    public void doError(Throwable throwable) {
                        requestsErrorsCounter.incrementAndGet();
                    }

                    @Override
                    public void processTimeElapsed(long time) {
                        requestsTimeElapsedCounter.getAndAdd(time);
                    }
                })
        );
        tinyServer.run();
        loadTestService.run();
        ConcurrentLinkedQueue<TinyStatisticsTask.TinyStatistics> tinyStatisticsQueue = new ConcurrentLinkedQueue<>();
        Timer statisticsTimer = new Timer("Timer");
        TinyStatisticsTask tinyStatisticsTask =
                new TinyStatisticsTask(requestsCounter, tinyStatisticsQueue, loggingStatistics);
        statisticsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tinyStatisticsTask.run();
            }
        }, 10L, 1000L);
        TinyStatisticsDumpService tinyStatisticsDumpService =
                new TinyStatisticsDumpService(
                        loggingStatistics,
                        requestsCounter,
                        tinyStatisticsQueue,
                        requestsErrorsCounter,
                        requestsTimeElapsedCounter
                );
        countDownLatch.await();
        sleep(10);
        assertEquals(iterations, requestsCounter.get());
        tinyServer.close();
        loadTestService.close();
        tinyStatisticsTask.run();
        tinyStatisticsDumpService.printStatistics();
    }

    private int generatePort() {
        return 8000 + random.nextInt(100);
    }
}
