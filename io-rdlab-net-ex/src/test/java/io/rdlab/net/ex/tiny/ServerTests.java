package io.rdlab.net.ex.tiny;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class ServerTests {
    @Test
    void simpleTest() {
        Executor executor = Executors.newSingleThreadExecutor();
        Server server = new Server(
                new InetSocketAddress(80),
                executor,
                exchange -> exchange.sendResponse(
                        200,
                        "Content-Type: text/plain",
                        "42"
                )
        );
        server.start();

        try (
                HttpClient httpClient = HttpClient.newHttpClient()
        ) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:80")).GET().build();
            await()
                    .pollInterval(10, TimeUnit.MILLISECONDS)
                    .timeout(2, TimeUnit.SECONDS)
                    .until(() -> {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        Optional<String> contentTypeOpt = response.headers().firstValue("content-type");
                        return response.statusCode() == 200 &&
                                "42".equals(response.body()) &&
                                contentTypeOpt.isPresent() &&
                                "Content-Type: text/plain".equals(contentTypeOpt.get());
                    });
        }
    }
}
