package io.rdlab.cons.ms;

import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static io.rdlab.cons.ms.Utils.getEnv;

public class TinyClientTerminal {
    private static final Logger LOG = LoggerFactory.getLogger(TinyClientTerminal.class);

    public static void run(String[] args) {
        int index = 0;
        while (index < args.length) {
            String arg = args[index];
            if ("-p".equals(arg)) {
                index++;
                String argValue = args[index];
                run(argValue);
                return;
            } else {
                index++;
            }
        }
    }

    public static void run() {
        String host = getEnv("CONS_HOST", "0.0.0.0");
        int port = Integer.parseInt(getEnv("CONS_PORT", "8003"));
        String command = getEnv("CONS_COMMAND", "l");
        if ("l".equals(command)) {
            long iterations = Long.parseLong(getEnv("CONS_ITERATIONS", "31"));
            long tokensCapacity = Long.parseLong(getEnv("CONS_TOKENS_CAPACITY", "10"));
            runSimpleLoadTest(host, port, iterations, tokensCapacity);
        }
    }

    private static void run(String propertiesFile) {
        String host;
        int port;
        String command;
        ConcurrentHashMap<Object, Object> allProperties;
        try (FileReader fileReader = new FileReader(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(fileReader);
            host = properties.getProperty("HOST", "localhost");
            port = Integer.parseInt(properties.getProperty("PORT", "8003"));
            command = properties.getProperty("COMMAND", "m");
            allProperties = new ConcurrentHashMap<>(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        run(host, port, command, allProperties);
    }

    private static void run(
            String host,
            int port,
            String command,
            ConcurrentHashMap<Object, Object> allProperties
    ) {
        if ("m".equals(command)) {
            try (TinyClient tinyClient = TinyClient.create(host, port, true)) {
                byte v = 2;
                LOG.info("Start m command.");
                TinyClient.Response response = tinyClient.exchange(new byte[]{v});
                System.out.printf("Request: %s, response: %s.%n", v, response.data()[0]);
            }
        } else if ("l".equals(command)) {
            long iterations = Long.parseLong(allProperties.getOrDefault("ITERATIONS", 31L).toString());
            long tokensCapacity =
                    Long.parseLong(allProperties.getOrDefault("TOKENS_CAPACITY", 10L).toString());
            runSimpleLoadTest(host, port, iterations, tokensCapacity);
        }
    }

    private static void runSimpleLoadTest(
            String host,
            int port,
            long iterations,
            long tokensCapacity
    ) {
        UnixOperatingSystemMXBean osMBean =
                (UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        LOG.info(
                "Start simple load test command. host: {}, port: {}, it: {}, c: {}.",
                host,
                port,
                iterations,
                tokensCapacity
        );
        LOG.info(
                "System. open: {}, max: {}.",
                osMBean.getOpenFileDescriptorCount(),
                osMBean.getMaxFileDescriptorCount()
        );
        boolean logging = false;
        boolean loggingStatistics = true;
        CountDownLatch requestsCountDownLatch = new CountDownLatch((int) iterations);
        AtomicLong requestsCounter = new AtomicLong();
        AtomicLong requestsErrorsCounter = new AtomicLong();
        AtomicLong requestsTimeElapsedCounter = new AtomicLong();
        LoadTestService loadTestService = new LoadTestService(
                tokensCapacity,
                iterations,
                Duration.ofSeconds(1),
                new RandomTaskGenerator(host, port, logging, new RandomTaskGenerator.Handler() {
                    @Override
                    public void doBefore() {
                        requestsCounter.incrementAndGet();
                        requestsCountDownLatch.countDown();
                    }

                    @Override
                    public void doAfter() {
                    }

                    @Override
                    public void doError(Throwable throwable) {
                        if (throwable.getMessage() != null && !throwable.getMessage().contains("Closed by interrupt")) {
                            requestsErrorsCounter.incrementAndGet();
                            LOG.error(throwable.getMessage(), throwable);
                        }
                    }

                    @Override
                    public void processTimeElapsed(long time) {
                        requestsTimeElapsedCounter.getAndAdd(time);
                    }
                })
        );
        loadTestService.run();
        ConcurrentLinkedQueue<TinyStatisticsTask.TinyStatistics> tinyStatisticsQueue = new ConcurrentLinkedQueue<>();
        TinyStatisticsDumpService tinyStatisticsDumpService =
                new TinyStatisticsDumpService(
                        loggingStatistics,
                        requestsCounter,
                        tinyStatisticsQueue,
                        requestsErrorsCounter,
                        requestsTimeElapsedCounter,
                        requestsCountDownLatch
                );
        Timer statisticsTimer = new Timer("Timer");
        TinyStatisticsTask tinyStatisticsTask =
                new TinyStatisticsTask(
                        requestsCounter,
                        tinyStatisticsQueue,
                        loggingStatistics,
                        tinyStatisticsDumpService
                );
        statisticsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tinyStatisticsTask.run();
            }
        }, 10L, 1000L);
        try {
            requestsCountDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        loadTestService.close();
        tinyStatisticsTask.run();
        tinyStatisticsDumpService.printStatistics();
        statisticsTimer.cancel();
    }
}
