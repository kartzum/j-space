package io.rdlab.cons.ms;

import static io.rdlab.cons.ms.Utils.getEnv;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            String name = args[0];
            String value = args[1];
            if (!"-e".equals(name)) {
                return;
            }
            if ("ts".equals(value)) {
                TinyServerTerminal.run(args);
            } else if ("tc".equals(value)) {
                TinyClientTerminal.run(args);
            }
        } else {
            String value = getEnv("CONS_N", "ts");
            if ("ts".equals(value)) {
                TinyServerTerminal.run();
            } else if ("tc".equals(value)) {
                TinyClientTerminal.run();
            }
        }
    }
}
