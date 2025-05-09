package io.rdlab.ic.mod;

import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

@SuppressWarnings("resource")
public abstract class ContainersRunner {
    private static final DockerComposeContainer<?> environment;

    static {
        environment = new DockerComposeContainer<>(
                new File("src/test/resources/docker-compose.yaml"));
        environment.start();
    }
}
