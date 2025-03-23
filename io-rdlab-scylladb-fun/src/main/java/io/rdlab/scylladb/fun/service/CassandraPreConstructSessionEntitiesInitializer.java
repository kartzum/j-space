package io.rdlab.scylladb.fun.service;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.springframework.stereotype.Service;

import static io.rdlab.scylladb.fun.constants.PropertyConstants.DATE;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.GROUP;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.NAME;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.PROPERTY;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.VALUE_STRING;

@Service
public class CassandraPreConstructSessionEntitiesInitializer implements CassandraPreConstructSessionInitializer {
    @Override
    public void execute(CqlSession session, CqlIdentifier keyspace) {
        session.execute(createPropertyTableStatement(keyspace));
    }

    private SimpleStatement createPropertyTableStatement(CqlIdentifier keyspace) {
        return SchemaBuilder.createTable(keyspace, PROPERTY)
                .ifNotExists()
                .withPartitionKey(GROUP, DataTypes.TEXT)
                .withPartitionKey(NAME, DataTypes.TEXT)
                .withClusteringColumn(DATE, DataTypes.TIMESTAMP)
                .withColumn(VALUE_STRING, DataTypes.TEXT)
                .withClusteringOrder(DATE, ClusteringOrder.DESC)
                .build();
    }
}
