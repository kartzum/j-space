package io.rdlab.cons.ms;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static io.rdlab.cons.ms.TestUtils.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadTestServiceTest {
    @Test
    void test() {
        long iterations = 5L;
        AtomicLong requestsCounter = new AtomicLong();
        LoadTestService loadTestService = new LoadTestService(
                10L,
                iterations,
                Duration.ofSeconds(1),
                () -> requestsCounter::incrementAndGet
        );
        loadTestService.run();
        sleep(1500);
        loadTestService.close();
        assertEquals(iterations, requestsCounter.get());
    }
}
