package io.rdlab.scylladb.fun.config;

import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.DriverConfigLoaderBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(value = "app.cassandra.driver.config.enabled", havingValue = "true")
public class CassandraDriverConfigLoaderBuilderCustomizer implements DriverConfigLoaderBuilderCustomizer {
    private static final String SEPARATOR = ",";

    @Value("${app.cassandra.driver.advanced.netty.io-group.size:#{null}}")
    private Integer nettyIoSize;

    @Value("${app.cassandra.driver.advanced.connection.max-requests-per-connection:#{null}}")
    private Integer maxRequests;

    @Value("${app.cassandra.driver.advanced.connection.pool.local.size:#{null}}")
    private Integer localSize;

    @Value("${app.cassandra.driver.advanced.connection.pool.remote.size:#{null}}")
    private Integer remoteSize;

    @Value("${app.cassandra.driver.advanced.metrics.factory:com.datastax.oss.driver.internal.metrics.micrometer.MicrometerMetricsFactory}")
    private String metricsFactory;

    @Value("${app.cassandra.driver.advanced.metrics.id-generator.class:com.datastax.oss.driver.internal.core.metrics.TaggingMetricIdGenerator}")
    private String idGenerator;

    @Value("${app.cassandra.driver.advanced.metrics.id-generator.prefix:cassandra}")
    private String idGeneratorPrefix;

    @Value("${app.cassandra.driver.advanced.metrics.session:#{null}}")
    private String metricsSession;

    @Value("${app.cassandra.driver.advanced.metrics.node:#{null}}")
    private String metricsNode;

    @Value("${app.cassandra.driver.basic.request.timeout:#{null}}")
    private String timeout;

    @Override
    public void customize(ProgrammaticDriverConfigLoaderBuilder builder) {
        if (nettyIoSize != null) {
            builder.withInt(DefaultDriverOption.NETTY_IO_SIZE, nettyIoSize);
        }

        if (maxRequests != null) {
            builder.withInt(DefaultDriverOption.CONNECTION_MAX_REQUESTS, maxRequests);
        }
        if (localSize != null) {
            builder.withInt(DefaultDriverOption.CONNECTION_POOL_LOCAL_SIZE, localSize);
        }
        if (remoteSize != null) {
            builder.withInt(DefaultDriverOption.CONNECTION_POOL_REMOTE_SIZE, remoteSize);
        }

        if (metricsFactory != null && !metricsFactory.isBlank()) {
            builder.withString(DefaultDriverOption.METRICS_FACTORY_CLASS,
                    metricsFactory);
        }
        if (idGenerator != null && !idGenerator.isBlank()) {
            builder.withString(DefaultDriverOption.METRICS_ID_GENERATOR_CLASS,
                    idGenerator);
        }
        if (idGeneratorPrefix != null && !idGeneratorPrefix.isBlank()) {
            builder.withString(DefaultDriverOption.METRICS_ID_GENERATOR_PREFIX,
                    idGeneratorPrefix);
        }
        if (metricsSession != null && !metricsSession.isBlank()) {
            builder.withStringList(DefaultDriverOption.METRICS_SESSION_ENABLED,
                    metricsSession.contains(SEPARATOR) ? Arrays.stream(metricsSession.split(SEPARATOR)).toList()
                            : List.of(metricsSession));
        }
        if (metricsNode != null && !metricsNode.isBlank()) {
            builder.withStringList(DefaultDriverOption.METRICS_NODE_ENABLED,
                    metricsNode.contains(SEPARATOR) ? Arrays.stream(metricsNode.split(SEPARATOR)).toList()
                            : List.of(metricsNode));
        }

        if (timeout != null && !timeout.isBlank()) {
            builder.withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.parse(timeout));
        }
    }
}
