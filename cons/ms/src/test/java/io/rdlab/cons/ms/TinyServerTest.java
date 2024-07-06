package io.rdlab.cons.ms;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.rdlab.cons.ms.TestUtils.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TinyServerTest {
    @Test
    void receiveData() {
        Random random = new Random();
        int port = 8000 + random.nextInt(100);
        TinyServer tinyServer = TinyServer.create("0.0.0.0", port, 1, new MultiplierHandler());
        tinyServer.run();
        TinyClient tinyClient = TinyClient.create("localhost", port);
        TinyClient.Response response = tinyClient.exchange(new byte[]{2});
        assertTrue(response.length() > 0);
        assertEquals(4, response.data()[0]);
        sleep(10);
        tinyServer.stop();
        tinyClient.stop();
    }
}
