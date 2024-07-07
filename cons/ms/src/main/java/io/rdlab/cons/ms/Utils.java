package io.rdlab.cons.ms;

public class Utils {
    public static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static double round(double value) {
        return Math.floor(value * 100) / 100;
    }
}
