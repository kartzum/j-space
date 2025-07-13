package io.rdlab.pr.tl.cl;

import io.rdlab.pr.tl.com.mqtt5.Mqtt5IntRun;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "pr-tl",
        mixinStandardHelpOptions = true,
        version = "pr-tl 0.0.1",
        description = "IoT/IIoT communication tools."
)
public class TlCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-t", "--tool"}, description = "mqtt-5-int, ...")
    private String tool = "mqtt-5-int";

    @Override
    public Integer call() {
        start();
        return new Mqtt5IntRun().run();
    }

    private void start() {
        System.out.println("Start " + tool + ".");
    }
}
