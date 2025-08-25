package io.rdlab.pr.tl.cl;

import io.rdlab.pr.tl.com.mqtt.Mqtt5ClientRunner;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "pr-tl",
        mixinStandardHelpOptions = true,
        version = "pr-tl 0.0.1",
        description = "IoT/IIoT communication tools."
)
public class TlCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-t", "--tool"}, description = "mqtt-5-int-runner, ...")
    private String tool = "mqtt-5-int-runner";

    @Override
    public Integer call() {
        start();
        if ("mqtt-5-int-runner".equals(tool)) {
            return new Mqtt5ClientRunner().run();
        }
        return 0;
    }

    private void start() {
        System.out.println("Start " + tool + ".");
    }
}
