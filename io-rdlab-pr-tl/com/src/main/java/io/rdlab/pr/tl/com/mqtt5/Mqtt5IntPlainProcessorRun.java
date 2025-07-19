package io.rdlab.pr.tl.com.mqtt5;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static io.rdlab.pr.tl.com.util.Utils.createInetAddress;

public class Mqtt5IntPlainProcessorRun {
    private Long counter = 0L;

    public Integer run(
            String host,
            int port,
            String user,
            String password,
            String topics,
            String number,
            boolean isLogInfoEnabled
    ) {
        long n = number != null ? Long.parseLong(number) : 1L;
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Void>> runs = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                runs.add(() -> {
                    Robot robot = new DefaultRobot();
                    runSingle(
                            host, port, user, password, topics,
                            new DefaultRobotSupplier(robot),
                            (++counter).toString(),
                            isLogInfoEnabled
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
            boolean isLogInfoEnabled
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
                id
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
                    "Finish. id = %s, ipt = %s, counts = %s, totalTime = %s.",
                    id, String.format("%.6f", itemPerTime), counts, totalTime
            ));
        }
    }

    private void logInfo(Object s) {
        System.out.println(s);
    }

    private record DefaultRobotSupplier(Robot robot) implements Supplier<Robot> {
        @Override
        public Robot get() {
            return robot;
        }
    }

    private static class DefaultRobot implements Robot {
        private final Random random = new Random();
        private long counter = 0;
        private long totalTime = 0;
        private boolean isLogInfoEnabled = true;

        @Override
        public Map<String, Object> calculate(Map<String, Object> data) {
            counter++;
            if (data == null) {
                if (isLogInfoEnabled) {
                    System.out.println(String.format("Empty data, counter = %s.", counter));
                }
                return Map.of();
            }
            if (data.get("data") instanceof byte[] dataAsArray) {
                String request = new String(dataAsArray, Charset.defaultCharset());
                long totalCounter = Long.parseLong(data.get("totalCounter").toString());
                String id = data.get("id").toString();
                String topic = data.get("topic").toString();
                if (counter > totalCounter) {
                    if (isLogInfoEnabled) {
                        System.out.println(String.format("Excess. id = %s, counter = %s.", id, counter));
                    }
                    return Map.of();
                } else {
                    String[] parts = !request.isEmpty() ? request.split(",") : new String[]{request};
                    Map<String, String> values = new LinkedHashMap<>();
                    int messageCounter = 0;
                    for (String part : parts) {
                        if (!part.isEmpty()) {
                            if (part.contains(":")) {
                                String[] keyValue = part.split(":");
                                values.put(keyValue[0], keyValue[1]);
                                if (keyValue[0].startsWith("key")) {
                                    messageCounter++;
                                }
                            }
                        }
                    }
                    String nextTopic = topic;
                    if (messageCounter > 2 && data.get("changeTopic") instanceof Boolean changeTopic && changeTopic) {
                        List<String> topics = (List<String>) data.get("topics");
                        nextTopic = topics.size() > 1 ?
                                topics.get(random.nextInt(topics.size() - 1)) : topics.getFirst();
                    }
                    long currentTime = System.currentTimeMillis();
                    long timeElapsed = 0;
                    if (values.get("time") instanceof String timeAsString) {
                        long time = Long.parseLong(timeAsString);
                        timeElapsed = currentTime - time;
                    }
                    StringBuilder responseBuilder = new StringBuilder();
                    values.forEach((k, v) -> {
                        if (!"time".equals(k)) {
                            responseBuilder.append(String.format("%s:%s,", k, v));
                        }
                    });
                    if (counter < 7) {
                        responseBuilder.append(String.format("key%s:%s", messageCounter, random.nextInt(9)));
                        responseBuilder.append(",");
                    }
                    responseBuilder.append(String.format("time:%s", currentTime));
                    String response = responseBuilder.toString();
                    if (isLogInfoEnabled) {
                        System.out.println(String.format(
                                "id = %s. Request = '%s' from topic = %s, response = '%s'." +
                                        " counter = %s, nextTopic = %s, timeElapsed = %s.",
                                id, request, topic, response, counter, nextTopic, timeElapsed
                        ));
                    }
                    totalTime += timeElapsed;
                    return Map.of(
                            "data", response.getBytes(),
                            "nextTopic", nextTopic,
                            "counts", counter,
                            "totalTime", totalTime
                    );
                }
            } else {
                if (isLogInfoEnabled) {
                    System.out.println(String.format("Empty array in data, counter = %s", counter));
                }
                return Map.of();
            }
        }
    }
}
