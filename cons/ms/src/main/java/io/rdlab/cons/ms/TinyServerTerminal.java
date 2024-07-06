package io.rdlab.cons.ms;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static io.rdlab.cons.ms.Utils.getEnv;

public class TinyServerTerminal {
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

    public static void run() {
        String host = getEnv("CONS_HOST", "0.0.0.0");
        int port = Integer.parseInt(getEnv("CONS_PORT", "8003"));
        run(host, port);
    }

    private static void run(String propertiesFile) {
        String host;
        int port;
        try (FileReader fileReader = new FileReader(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(fileReader);
            host = properties.getProperty("HOST", "0.0.0.0");
            port = Integer.parseInt(properties.getProperty("PORT", "8003"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        run(host, port);
    }

    private static void run(
            String host,
            int port
    ) {
        TinyServer tinyServer = TinyServer.create(host, port, 1, new MultiplierHandler());
        tinyServer.run();
    }
}
