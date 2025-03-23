package io.rdlab.scylladb.fun.service;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;

import java.net.InetSocketAddress;

public class MigrationTask {
    private final CassandraProperties properties;
    private final CassandraConnectionDetails connectionDetails;
    private final DriverConfigLoader driverConfigLoader;

    private final ObjectProvider<CassandraPreConstructSessionInitializer> sessionInitializers;

    public MigrationTask(
            CassandraProperties properties,
            CassandraConnectionDetails connectionDetails,
            DriverConfigLoader driverConfigLoader,
            ObjectProvider<CassandraPreConstructSessionInitializer> sessionInitializers
    ) {
        this.properties = properties;
        this.connectionDetails = connectionDetails;
        this.driverConfigLoader = driverConfigLoader;
        this.sessionInitializers = sessionInitializers;
    }

    public void migrate() {
        try (CqlSession session = createCqlSession()) {
            session.execute(createKeyspaceStatement(properties.getKeyspaceName()));
            CqlIdentifier keyspace = CqlIdentifier.fromInternal(properties.getKeyspaceName());
            sessionInitializers.orderedStream().forEach(i -> i.execute(session, keyspace));
        }
    }

    private CqlSession createCqlSession() {
        CqlSessionBuilder sessionBuilder = CqlSession.builder().withConfigLoader(driverConfigLoader);
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
}
