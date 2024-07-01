package io.rdlab.cons.coap.californium.con;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;

public class TestUtils {
    public static CoapClient createClient(String host, int port, String path, Endpoint endpoint) {
        CoapClient client = new CoapClient();
        client.setURI("coap://" + host + ":" + port + "/" + path);
        client.setEndpoint(endpoint);
        return client;
    }

    public static Endpoint createEndpoint() {
        return CoapEndpoint.builder().build();
    }
}
