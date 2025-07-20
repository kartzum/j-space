package io.rdlab.pr.tl.com.mqtt5;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static io.rdlab.pr.tl.com.util.SourceUtils.compileAndLoad;
import static io.rdlab.pr.tl.com.util.SourceUtils.loadTextFromFile;
import static io.rdlab.pr.tl.com.util.Utils.createInetAddress;

public class Mqtt5IntPlainProcessorRun {
    private Long counter = 0L;

    public Integer run(
            String host,
            int port,
            String user,
            String password,
            String topics,
            String threads,
            boolean isLogInfoEnabled,
            String dir,
            long total
    ) {
        long n = threads != null ? Long.parseLong(threads) : 1L;
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Void>> runs = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                runs.add(() -> {
                    Supplier<Robot> supplier = calculateRobotSupplier(
                            dir, Map.of("isLogInfoEnabled", isLogInfoEnabled)
                    );
                    runSingle(
                            host, port, user, password, topics,
                            supplier,
                            (++counter).toString(),
                            isLogInfoEnabled,
                            total
                    );
                    return null;
                });
            }
            try {
                executorService.invokeAll(runs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    private void runSingle(
            String host,
            int port,
            String user,
            String password,
            String topics,
            Supplier<Robot> robotSupplier,
            String id,
            boolean isLogInfoEnabled,
            long total
    ) {
        if (isLogInfoEnabled) {
            logInfo("Start. id = " + id +
                    ". Host = " + host + ", port = " + port + ", user = " + user + ", topics = " + topics + ".");
        }
        Mqtt5IntPlainProcessor mqtt5IntPlainProcessor = new Mqtt5IntPlainProcessor();
        mqtt5IntPlainProcessor.run(
                createInetAddress(host),
                port,
                user,
                password,
                topics,
                robotSupplier,
                id,
                total
        );
        double itemPerTime = 0;
        long counts = 0;
        long totalTime = 0;
        if (mqtt5IntPlainProcessor.getTotalTime() != 0) {
            counts = mqtt5IntPlainProcessor.getCounts();
            totalTime = mqtt5IntPlainProcessor.getTotalTime();
            itemPerTime = (double) counts / (double) totalTime;
        }
        if (isLogInfoEnabled) {
            logInfo(String.format(
                    "Finish. id = %s, ipt = %s, %s, counts = %s, totalTime = %s.",
                    id, String.format("%.6f", itemPerTime), String.format("%.6f", itemPerTime * 1000), counts, totalTime
            ));
        }
    }

    private void logInfo(Object s) {
        System.out.println(s);
    }

    private Supplier<Robot> calculateRobotSupplier(String dir, Map<String, Object> params) {
        if (dir == null || dir.isEmpty()) {
            return new DefaultRobotSupplier(new DefaultRobot(params));
        }
        try {
            Path path = Paths.get(dir, "Mqtt5IntRobot.java");
            String sourceCode = loadTextFromFile(path);
            Class<?> robotClass = compileAndLoad("Mqtt5IntRobot", sourceCode);
            Constructor<?> ctor = robotClass.getConstructor(Map.class);
            Robot robot = (Robot) ctor.newInstance(params);
            return new DefaultRobotSupplier(robot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private record DefaultRobotSupplier(Robot robot) implements Supplier<Robot> {
        @Override
        public Robot get() {
            return robot;
        }
    }
}
