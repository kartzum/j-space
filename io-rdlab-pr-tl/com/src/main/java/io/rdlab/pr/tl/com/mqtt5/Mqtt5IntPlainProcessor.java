package io.rdlab.pr.tl.com.mqtt5;

import org.awaitility.Duration;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static io.rdlab.pr.tl.com.util.Utils.await;

public class Mqtt5IntPlainProcessor {
    private Runner runner;

    public void run(
            InetAddress host,
            int port,
            String user,
            String password,
            String topics,
            Supplier<Robot> robotSupplier,
            String id
    ) {
        if (runner != null) {
            runner.close();
        }
        runner = new Runner();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            runner.start(host, port, user, password, topics, robotSupplier, id);
            executorService.submit(() -> {
                do {
                    runner.run();
                    do {
                        await(new Duration(1, TimeUnit.MILLISECONDS));
                        if (runner.isRestart()) {
                            break;
                        }
                    } while (!runner.isStop());
                    if (runner.isRestart()) {
                        runner.unsubscribe();
                        runner.changeTopic();
                    }
                } while (!runner.isStop());
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            runner.close();
        }
    }

    private static class Runner {
        private Mqtt5Int mqtt5Int;
        private List<String> topics;
        private Supplier<Robot> robotSupplier;
        private volatile boolean stop;
        private volatile boolean restart;
        private volatile String topic;
        private volatile String nextTopic;
        private String id;

        public void start(
                InetAddress host,
                int port,
                String user,
                String password,
                String topics,
                Supplier<Robot> robotSupplier,
                String id
        ) {
            this.topics = !topics.contains(",") ? List.of(topics) : Arrays.stream(topics.split(",")).toList();
            this.robotSupplier = robotSupplier;
            this.mqtt5Int = new Mqtt5Int();
            this.mqtt5Int.start(host, port);
            this.mqtt5Int.connect(user, password);
            this.topic = this.topics.getFirst();
            this.id = id;
        }

        public void run() {
            mqtt5Int.subscribe(topic, publish -> {
                Map<String, Object> result = null;
                if (!stop) {
                    result = robotSupplier.get().calculate(
                            Map.of(
                                    "data", publish.data(),
                                    "topic", topic,
                                    "topics", topics,
                                    "totalCounter", 15L,
                                    "changeTopic", true,
                                    "id", id
                            )
                    );
                }
                if (result != null) {
                    if (result.get("data") instanceof byte[] dataAsArray) {
                        if (result.get("nextTopic") instanceof String nextTopicInner) {
                            if (!topic.equals(nextTopicInner)) {
                                restart = true;
                                this.nextTopic = nextTopicInner;
                            } else {
                                mqtt5Int.publish(topic, dataAsArray);
                            }
                        } else {
                            mqtt5Int.publish(topic, dataAsArray);
                        }
                    } else {
                        stop = true;
                    }
                } else {
                    stop = true;
                }
            });
            mqtt5Int.publish(topic, new byte[0]);
        }

        public boolean isStop() {
            return stop;
        }

        public boolean isRestart() {
            return restart;
        }

        public void close() {
            unsubscribe();
            mqtt5Int.disconnect();
        }

        public void unsubscribe() {
            mqtt5Int.unsubscribe(topic);
        }

        public void changeTopic() {
            topic = nextTopic;
            restart = false;
            nextTopic = null;
        }
    }
}
