package io.rdlab.pr.tl.com.mqtt5;

import java.net.InetAddress;
import java.util.function.Supplier;

public class Mqtt5IntPlainExecutor {
    private Mqtt5Int mqtt5Int;
    private String topic;
    private Supplier<Robot> robotSupplier;
    private volatile boolean stop;

    public void start(
            InetAddress host,
            int port,
            String user,
            String password,
            String topic,
            Supplier<Robot> robotSupplier
    ) {
        this.topic = topic;
        this.robotSupplier = robotSupplier;
        this.mqtt5Int = new Mqtt5Int();
        this.mqtt5Int.start(host, port);
        this.mqtt5Int.connect(user, password);
    }

    public void execute() {
        mqtt5Int.subscribe(topic, publish -> {
            byte[] result = null;
            if (!stop) {
                result = robotSupplier.get().calculate(publish.data());
            }
            if (result != null && result.length > 0) {
                mqtt5Int.publish(topic, result);
            } else {
                stop = true;
            }
        });
        mqtt5Int.publish(topic, new byte[0]);
    }

    public boolean isStop() {
        return stop;
    }

    public void close() {
        mqtt5Int.unsubscribe(topic);
        mqtt5Int.disconnect();
    }

    public interface Robot {
        byte[] calculate(byte[] data);
    }
}
