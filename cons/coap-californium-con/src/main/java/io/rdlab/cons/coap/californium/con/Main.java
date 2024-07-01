package io.rdlab.cons.coap.californium.con;

import io.rdlab.cons.coap.californium.con.simple.ServerTerminal;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }
        String name = args[0];
        String value = args[1];
        if (!"-e".equals(name)) {
            return;
        }
        if ("scs".equals(value)) {
            ServerTerminal.run(args);
        }
    }
}
