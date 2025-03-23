package io.rdlab.scylladb.fun;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

@SuppressWarnings("resource")
public abstract class ContainersRunner {
    private static final DockerComposeContainer<?> environment;

    static {
        environment = new DockerComposeContainer<>(
                new File("src/test/resources/docker-compose.yaml"))
                .withExposedService("scylla", 9042, Wait.forListeningPort());
        environment.start();
    }
}
