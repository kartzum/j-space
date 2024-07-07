package io.rdlab.cons.coap.californium.con.util;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    public static void setRootLogLevel(Level newLevel) {
        Logger root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        ((ch.qos.logback.classic.Logger) root).setLevel(newLevel);
    }

    public static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
}
