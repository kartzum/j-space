package io.rdlab.scylladb.fun.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;

import java.net.InetSocketAddress;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {
    private final CassandraProperties properties;
    private final CassandraConnectionDetails connectionDetails;

    public CassandraConfig(CassandraProperties properties, CassandraConnectionDetails connectionDetails) {
        this.properties = properties;
        this.connectionDetails = connectionDetails;
    }

    @Override
    protected String getKeyspaceName() {
        return properties.getKeyspaceName();
    }

    @Override
    protected String getLocalDataCenter() {
        return properties.getLocalDatacenter();
    }

    @Override
    protected int getPort() {
        return properties.getPort();
    }

    @Override
    protected String getContactPoints() {
        return String.join(",", properties.getContactPoints());
    }

    @Bean
    @Override
    public CqlSessionFactoryBean cassandraSession() {
        prepareSession();
        return super.cassandraSession();
    }

    private void prepareSession() {
        try (CqlSession session = createCqlSession()) {
            session.execute(createKeyspaceStatement(properties.getKeyspaceName()));
            session.execute("USE " + properties.getKeyspaceName());
            createResourceKeyspacePopulator().populate(session);
        }
    }

    private CqlSession createCqlSession() {
        CqlSessionBuilder sessionBuilder = CqlSession.builder();
        for (CassandraConnectionDetails.Node node : connectionDetails.getContactPoints()) {
            InetSocketAddress address = InetSocketAddress.createUnresolved(node.host(), node.port());
            sessionBuilder = sessionBuilder.addContactPoint(address);
        }
        return sessionBuilder.withLocalDatacenter(connectionDetails.getLocalDatacenter()).build();
    }

    private SimpleStatement createKeyspaceStatement(String keyspace) {
        return SchemaBuilder.createKeyspace(keyspace)
                .ifNotExists()
                .withSimpleStrategy(1)
                .withDurableWrites(false)
                .build();
    }

    private ResourceKeyspacePopulator createResourceKeyspacePopulator() {
        return new ResourceKeyspacePopulator(
                new ClassPathResource("cassandra/migration/001_most_common_text_data.cql"),
                new ClassPathResource("cassandra/migration/002_most_common_text_accumulate.cql"),
                new ClassPathResource("cassandra/migration/003_most_common_text_calculate.cql"),
                new ClassPathResource("cassandra/migration/004_most_common_text.cql"),
                new ClassPathResource("cassandra/migration/005_property.cql")
        );
    }
}
