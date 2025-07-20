package io.rdlab.pr.tl.com.mqtt5;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DefaultRobot implements Robot {
    private final Random random = new Random();
    private final boolean isLogInfoEnabled;
    private long counter = 0;
    private long totalTime = 0;

    public DefaultRobot(Map<String, Object> params) {
        this.isLogInfoEnabled = (boolean) params.get("isLogInfoEnabled");
    }

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
