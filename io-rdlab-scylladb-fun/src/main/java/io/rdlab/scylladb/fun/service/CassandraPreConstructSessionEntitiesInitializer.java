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
        session.execute("USE " + keyspace);

        session.execute(createPropertyTableStatement(keyspace));

        session.execute(mostCommonDataTextType());
        session.execute(mostCommonTextAccumulate());
        session.execute(mostCommonTextCalculate());
        session.execute(mostCommonText());
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

    private String mostCommonDataTextType() {
        return "create type if not exists most_common_text_data ( text_data frozen<map<text, bigint>> );";
    }

    private String mostCommonTextAccumulate() {
        return """
                CREATE OR REPLACE FUNCTION most_common_text_accumulate(storage most_common_text_data, val text)
                RETURNS NULL ON NULL INPUT
                RETURNS most_common_text_data
                LANGUAGE lua
                AS $$
                    if storage == nil then
                        storage = {}
                    end
                    if storage.text_data == nil then
                        storage.text_data = {}
                    end
                    if val == nil then
                        return storage
                    end
                    if storage.text_data[val] == nil then
                        storage.text_data[val] = 1
                    else
                        storage.text_data[val] = storage.text_data[val] + 1
                    end
                    return storage
                $$;
                """;
    }

    private String mostCommonTextCalculate() {
        return """
                CREATE OR REPLACE FUNCTION most_common_text_calculate(storage most_common_text_data)
                RETURNS NULL ON NULL INPUT
                RETURNS text
                LANGUAGE lua AS $$
                    if storage == nil or storage.text_data == nil then
                        return nil
                    end
                    local value = nil
                    local count = 0
                    for v, c in pairs(storage.text_data) do
                        if c > count then
                            value = v
                            count = c
                        end
                    end
                    return value
                $$;
                """;
    }

    private String mostCommonText() {
        return """
                CREATE OR REPLACE AGGREGATE most_common_text(text)
                   SFUNC most_common_text_accumulate
                   STYPE most_common_text_data
                   FINALFUNC most_common_text_calculate
                   INITCOND {text_data: {}};
                """;
    }
}
