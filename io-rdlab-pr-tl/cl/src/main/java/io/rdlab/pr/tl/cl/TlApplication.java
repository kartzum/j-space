package io.rdlab.pr.tl.cl;

import picocli.CommandLine;

public class TlApplication {
    public static void main(String[] args) {
        System.exit(new CommandLine(new TlCommand()).execute(args));
    }
}
