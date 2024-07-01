package io.rdlab.cons.coap.californium.con.simple;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.rdlab.cons.coap.californium.con.TestUtils.createClient;
import static io.rdlab.cons.coap.californium.con.TestUtils.createEndpoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleCoapServerTest {
    @Test
    void postTest() throws ConnectorException, IOException {
        SimpleCoapServer simpleCoapServer = null;
        String host = "0.0.0.0";
        int port = 7012;
        Configuration configuration = Configuration.createStandardWithoutFile();
        String clientHost = "localhost";
        Endpoint endpoint = null;
        CoapClient coapClient;
        try {
            simpleCoapServer = SimpleCoapServer.create(
                    host,
                    port,
                    configuration,
                    List.of(
                            new Benchmark(false, 2, 0)
                    )
            );
            simpleCoapServer.start();

            endpoint = createEndpoint();
            coapClient = createClient(clientHost, port, Benchmark.RESOURCE_NAME + "?rlen=2", endpoint);
            CoapResponse coapResponse = coapClient.post("{}", MediaTypeRegistry.TEXT_PLAIN);
            assertTrue(coapResponse.getCode().isSuccess());
            assertEquals("he", coapResponse.getResponseText());
        } finally {
            if (simpleCoapServer != null) {
                simpleCoapServer.stop();
            }
            if (endpoint != null) {
                endpoint.destroy();
            }
        }
    }
}
