package io.rdlab.pr.tl.cl;

import picocli.CommandLine;

public class TlApplication {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new TlCommand()).execute(args);
        System.exit(exitCode);
    }
}
