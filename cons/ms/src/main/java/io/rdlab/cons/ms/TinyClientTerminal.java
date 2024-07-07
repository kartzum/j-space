package io.rdlab.cons.ms;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TinyClientTerminal {
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

    private static void run(String propertiesFile) {
        String host;
        int port;
        String command;
        try (FileReader fileReader = new FileReader(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(fileReader);
            host = properties.getProperty("HOST", "176.109.101.82");
            port = Integer.parseInt(properties.getProperty("PORT", "8003"));
            command = properties.getProperty("COMMAND", "m");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        run(host, port, command);
    }

    private static void run(
            String host,
            int port,
            String command
    ) {
        if ("m".equals(command)) {
            try (TinyClient tinyClient = TinyClient.create(host, port, true)) {
                byte v = 2;
                TinyClient.Response response = tinyClient.exchange(new byte[]{v});
                System.out.printf("Request: %s, response: %s.%n", v, response.data()[0]);
            }
        } else if ("l".equals(command)) {

        }
    }
}
