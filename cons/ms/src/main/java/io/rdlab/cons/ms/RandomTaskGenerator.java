package io.rdlab.cons.ms;

import java.util.Random;

public class RandomTaskGenerator implements LoadTestService.TaskGenerator {
    private final String host;
    private final int port;
    private final boolean logging;
    private final Handler handler;

    public RandomTaskGenerator(
            String host,
            int port,
            boolean logging,
            Handler handler
    ) {
        this.host = host;
        this.port = port;
        this.logging = logging;
        this.handler = handler;
    }

    @Override
    public LoadTestService.Task generate() {
        return new LoadTestService.Task() {
            private final Random random = new Random();

            @Override
            public void run() {
                try {
                    handler.doBefore();
                    long start = System.currentTimeMillis();
                    try (TinyClient tinyClient = TinyClient.create(host, port, logging)) {
                        tinyClient.exchange(new byte[]{(byte) random.nextInt()});
                    }
                    long finish = System.currentTimeMillis();
                    handler.processTimeElapsed(finish - start);
                } catch (Throwable throwable) {
                    handler.doError(throwable);
                } finally {
                    handler.doAfter();
                }
            }
        };
    }

    public interface Handler {
        void doBefore();

        void doAfter();

        void doError(Throwable throwable);

        void processTimeElapsed(long time);
    }
}
