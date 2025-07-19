package io.rdlab.pr.tl.com.mqtt5;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Mqtt5Int {
    private Mqtt5AsyncClient mqtt5AsyncClient;

    public Result start(InetAddress host, int port) {
        mqtt5AsyncClient = MqttClient
                .builder()
                .useMqttVersion5()
                .identifier(UUID.randomUUID().toString())
                .serverHost(host)
                .serverPort(port)
                .buildAsync();
        return new Result("Created. host = " + host + ", port = " + port + ".");
    }

    public Result connect(String user, String password) {
        mqtt5AsyncClient.connectWith()
                .simpleAuth()
                .username(user)
                .password(password.getBytes())
                .applySimpleAuth()
                .send()
                .join();
        return new Result("Connected. user = " + user + ".");
    }

    public Result subscribe(String topic, Consumer<Publish> callback) {
        mqtt5AsyncClient.subscribeWith()
                .topicFilter(topic)
                .callback(mqtt5Publish -> {
                    callback.accept(new Publish(mqtt5Publish.getPayloadAsBytes()));
                })
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .send()
                .join();
        return new Result("Subscribed. topic = " + topic + ".");
    }

    public Result publish(String topic, byte[] payload) {
        mqtt5AsyncClient.publishWith()
                .topic(topic)
                .payload(payload)
                .send()
                .join();
        return new Result("Published. topic = " + topic + ".");
    }

    public Result unsubscribe(String topic) {
        mqtt5AsyncClient.unsubscribeWith()
                .topicFilter(topic)
                .send()
                .join();
        return new Result("Unsubscribed. topic = " + topic + ".");
    }

    public Result disconnect() {
        mqtt5AsyncClient.disconnect().join();
        mqtt5AsyncClient = null;
        return new Result("Disconnected.");
    }

    public record Result(String description) {
    }

    public record Publish(byte[] data) {
    }
}
