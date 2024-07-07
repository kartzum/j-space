package io.rdlab.cons.coap.californium.con.simple;

import ch.qos.logback.classic.Level;
import org.eclipse.californium.elements.config.Configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static io.rdlab.cons.coap.californium.con.util.Utils.getEnv;
import static io.rdlab.cons.coap.californium.con.util.Utils.setRootLogLevel;

public class ServerTerminal {
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
        int port = Integer.parseInt(getEnv("CONS_PORT", "7012"));
        run(new ServerParams(
                host,
                port
        ));
    }

    private static void run(String propertiesFile) {
        String host;
        int port;
        try (FileReader fileReader = new FileReader(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(fileReader);
            host = properties.getProperty("HOST", "0.0.0.0");
            port = Integer.parseInt(properties.getProperty("PORT", "7012"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        run(new ServerParams(
                host,
                port
        ));
    }

    private static void run(ServerParams serverParams) {
        setRootLogLevel(Level.INFO);
        Configuration configuration = Configuration.createStandardWithoutFile();
        SimpleCoapServer simpleCoapServer = SimpleCoapServer.create(
                serverParams.host,
                serverParams.port,
                configuration,
                List.of(
                        new Benchmark(false, 2, 0)
                )
        );
        simpleCoapServer.start();
    }

    private record ServerParams(String host, int port) {
    }
}
