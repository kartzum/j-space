package io.rdlab.pr.tl.com.mqtt5;

import org.awaitility.Duration;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static io.rdlab.pr.tl.com.util.Utils.createInetAddress;
import static io.rdlab.pr.tl.com.util.Utils.await;

public class Mqtt5IntPlainExecutorRun {
    public Integer run(
            String host,
            int port,
            String user,
            String password,
            String topic,
            Supplier<Mqtt5IntPlainExecutor.Robot> robotSupplier
    ) {
        println("Start. Host = " + host + ", port = " + port + ", user = " + user + ", topic = " + topic + ".");
        Supplier<Mqtt5IntPlainExecutor.Robot> calculatedRobotSupplier = robotSupplier != null ?
                robotSupplier : defaultRobotSupplier();
        Mqtt5IntPlainExecutor mqtt5IntPlainExecutor = new Mqtt5IntPlainExecutor();
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.submit(() -> {
                mqtt5IntPlainExecutor.start(
                        createInetAddress(host),
                        port,
                        user,
                        password,
                        topic,
                        calculatedRobotSupplier
                );
                mqtt5IntPlainExecutor.execute();
                do {
                    await(new Duration(1, TimeUnit.MILLISECONDS));
                } while (!mqtt5IntPlainExecutor.isStop());
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mqtt5IntPlainExecutor.close();
        }
        println("Finish.");
        return 0;
    }

    private Supplier<Mqtt5IntPlainExecutor.Robot> defaultRobotSupplier() {
        return () -> new Mqtt5IntPlainExecutor.Robot() {
            private final Random random = new Random();

            @Override
            public byte[] calculate(byte[] data) {
                if (data == null || data.length == 0) {
                    System.out.println("Process empty request.");
                    String response = "" + random.nextInt();
                    System.out.println("Response = " + response + ".");
                    return response.getBytes();
                } else {
                    String request = new String(data, Charset.defaultCharset());
                    System.out.println("Process request = " + request + ".");
                    if (request.length() > 15) {
                        System.out.println("Excess.");
                        return new byte[0];
                    }
                    String response = request + "_" + random.nextInt();
                    System.out.println("Response = " + response + ".");
                    return response.getBytes();
                }
            }
        };
    }

    private void println(Object s) {
        System.out.println(s);
    }
}
