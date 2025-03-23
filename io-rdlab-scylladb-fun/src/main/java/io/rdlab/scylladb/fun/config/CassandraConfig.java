package io.rdlab.scylladb.fun.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import io.rdlab.scylladb.fun.service.CassandraPreConstructSessionInitializer;
import io.rdlab.scylladb.fun.service.MigrationTask;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cassandra.CassandraConnectionDetails;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.config.DefaultCqlBeanNames;

@Configuration
public class CassandraConfig {
    @Bean(DefaultCqlBeanNames.SESSION)
    @Primary
    @DependsOn("migrationTask")
    public CqlSession cassandraSession(CqlSessionBuilder cqlSessionBuilder) {
        return cqlSessionBuilder.build();
    }

    @Bean(initMethod = "migrate")
    public MigrationTask migrationTask(
            CassandraProperties properties,
            CassandraConnectionDetails connectionDetails,
            DriverConfigLoader driverConfigLoader,
            ObjectProvider<CassandraPreConstructSessionInitializer> sessionInitializers
    ) {
        return new MigrationTask(properties, connectionDetails, driverConfigLoader, sessionInitializers);
    }
}
