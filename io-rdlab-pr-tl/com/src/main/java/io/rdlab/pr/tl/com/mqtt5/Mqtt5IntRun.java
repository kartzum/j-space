package io.rdlab.pr.tl.com.mqtt5;

import java.nio.charset.Charset;
import java.util.Scanner;

import static io.rdlab.pr.tl.com.util.Utils.createInetAddress;

public class Mqtt5IntRun {
    private static final String EXIT = "exit";
    private static final String START = "start";
    private static final String CONNECT = "connect";
    private static final String SUBSCRIBE = "subscribe";
    private static final String PUBLISH = "publish";
    private static final String UNSUBSCRIBE = "unsubscribe";
    private static final String DISCONNECT = "disconnect";

    public Integer run() {
        Mqtt5Int mqtt5Int = new Mqtt5Int();
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\\n");
        String command;
        do {
            print(">");
            command = scanner.next();
            switch (command) {
                case START: {
                    print(">Enter host (default: 0.0.0.0).>");
                    String host = scanner.next();
                    print(">Enter port (default: 1883).>");
                    String port = scanner.next();
                    String calculatedHost = (host == null || host.isEmpty() || "_".equals(host)) ? "0.0.0.0" : host;
                    int calculatedPort =
                            (port == null || port.isEmpty() || "_".equals(port)) ? 1883 : Integer.parseInt(port);
                    println(mqtt5Int.start(createInetAddress(calculatedHost), calculatedPort));
                    break;
                }
                case CONNECT: {
                    print(">Enter user (default: u).>");
                    String user = scanner.next();
                    print(">Enter password (default: p).>");
                    String password = scanner.next();
                    String calculatedUser = (user == null || user.isEmpty() || "_".equals(user)) ? "u" : user;
                    String calculatePassword =
                            (password == null || password.isEmpty() || "_".equals(password)) ? "p" : password;
                    println(mqtt5Int.connect(calculatedUser, calculatePassword));
                    break;
                }
                case SUBSCRIBE: {
                    print(">Enter topic (default: thing/com).>");
                    String topic = scanner.next();
                    String calculatedTopic =
                            (topic == null || topic.isEmpty() || "_".equals(topic)) ? "thing/com" : topic;
                    println(mqtt5Int.subscribe(calculatedTopic, publish -> {
                        String dataAsString = new String(publish.data(), Charset.defaultCharset());
                        println(dataAsString);
                        print(">");
                    }));
                    break;
                }
                case PUBLISH: {
                    print(">Enter topic (default: thing/com).>");
                    String topic = scanner.next();
                    String calculatedTopic =
                            (topic == null || topic.isEmpty() || "_".equals(topic)) ? "thing/com" : topic;
                    String data = scanner.next();
                    println(
                            mqtt5Int.publish(calculatedTopic, data.getBytes(Charset.defaultCharset()))
                    );
                    break;
                }
                case UNSUBSCRIBE: {
                    print(">Enter topic (default: thing/com).>");
                    String topic = scanner.next();
                    String calculatedTopic =
                            (topic == null || topic.isEmpty() || "_".equals(topic)) ? "thing/com" : topic;
                    println(
                            mqtt5Int.unsubscribe(calculatedTopic)
                    );
                    break;
                }
                case DISCONNECT: {
                    println(
                            mqtt5Int.disconnect()
                    );
                    break;
                }
            }
        } while (!EXIT.equals(command));
        return 0;
    }

    private void print(Object s) {
        System.out.print(s);
    }

    private void println(Object s) {
        System.out.println(s);
    }
}
