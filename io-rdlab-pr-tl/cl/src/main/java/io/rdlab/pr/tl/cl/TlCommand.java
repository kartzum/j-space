package io.rdlab.pr.tl.cl;

import io.rdlab.pr.tl.com.mqtt5.Mqtt5IntPlainProcessorRun;
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
    @CommandLine.Option(names = {"-t", "--tool"}, description = "mqtt-5-int-run, ...")
    private String tool = "mqtt-5-int-run";

    @CommandLine.Option(names = {"-h", "--host"}, description = "host")
    private String host = "0.0.0.0";

    @CommandLine.Option(names = {"-r", "--port"}, description = "port")
    private String port = "1883";

    @CommandLine.Option(names = {"-u", "--user"}, description = "user")
    private String user = "u";

    @CommandLine.Option(names = {"-p", "--password"}, description = "password")
    private String password = "p";

    @CommandLine.Option(names = {"-c", "--topic"}, description = "topics")
    private String topics = "thing/com";

    @CommandLine.Option(names = {"-n", "--threads"}, description = "threads")
    private String threads = "1";

    @CommandLine.Option(names = {"-l", "--total"}, description = "total")
    private String total = "12";

    @CommandLine.Option(names = {"-d", "--dir"}, description = "dir")
    private String dir = ""; //"/tmp/.pr-tl/default";

    @Override
    public Integer call() {
        start();
        if ("mqtt-5-int-run".equals(tool)) {
            return new Mqtt5IntRun().run();
        } else if ("mqtt-5-int-pe".equals(tool)) {
            return new Mqtt5IntPlainProcessorRun().run(
                    host,
                    Integer.parseInt(port),
                    user,
                    password,
                    topics,
                    threads,
                    true,
                    dir,
                    Long.parseLong(total)
            );
        }
        return 0;
    }

    private void start() {
        System.out.println("Start " + tool + ".");
    }
}
