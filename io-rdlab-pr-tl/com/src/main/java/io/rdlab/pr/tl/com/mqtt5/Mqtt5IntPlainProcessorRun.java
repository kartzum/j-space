package io.rdlab.pr.tl.com.mqtt5;

import java.nio.charset.Charset;
import java.util.ArrayList;
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
            String number
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
                            (++counter).toString()
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
            String id
    ) {
        logInfo("Start. id = " + id +
                ". Host = " + host + ", port = " + port + ", user = " + user + ", topics = " + topics + ".");
        new Mqtt5IntPlainProcessor().run(
                createInetAddress(host),
                port,
                user,
                password,
                topics,
                robotSupplier,
                id
        );
        logInfo("Finish. id = " + id + ".");
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

        @Override
        public Map<String, Object> calculate(Map<String, Object> data) {
            counter++;
            if (data == null) {
                logInfo(String.format("Empty data, counter = %s.", counter));
                return Map.of();
            }
            if (data.get("data") instanceof byte[] dataAsArray) {
                String request = new String(dataAsArray, Charset.defaultCharset());
                long totalCounter = Long.parseLong(data.get("totalCounter").toString());
                String id = data.get("id").toString();
                String topic = data.get("topic").toString();
                if (counter > totalCounter) {
                    logInfo(String.format("Excess. id = %s, counter = %s.", id, counter));
                    return Map.of();
                } else {
                    String[] messages = !request.isEmpty() ? request.split(",") : new String[]{request};
                    String nextTopic = topic;
                    if (messages.length > 2 && data.get("changeTopic") instanceof Boolean changeTopic && changeTopic) {
                        List<String> topics = (List<String>) data.get("topics");
                        nextTopic = topics.size() > 1 ?
                                topics.get(random.nextInt(topics.size() - 1)) : topics.getFirst();
                    }
                    String response = !request.isEmpty() ?
                            request + "," + random.nextInt(9) : "" + random.nextInt(9);
                    logInfo(String.format(
                            "Calculated. id = %s. Request = '%s' from topic = %s, response = '%s'. counter = %s, nextTopic = %s.",
                            id, request, topic, response, counter, nextTopic
                    ));
                    return Map.of(
                            "data", response.getBytes(),
                            "nextTopic", nextTopic
                    );
                }
            } else {
                logInfo(String.format("Empty array in data, counter = %s", counter));
                return Map.of();
            }
        }

        private void logInfo(Object s) {
            System.out.println(s);
        }
    }
}
