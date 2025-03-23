package io.rdlab.scylladb.fun.repository;

import com.datastax.oss.driver.api.core.AsyncPagingIterable;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import io.rdlab.scylladb.fun.model.Property;
import io.rdlab.scylladb.fun.service.RowCollector;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.relation.Relation.column;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.DATE;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.END;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.GROUP;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.NAME;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.PROPERTY;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.START;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.VALUE_STRING;

@Repository
public class PropertyRepository {
    private final CqlSession session;
    private final CqlIdentifier keyspace;

    private final Function<Row, Property> rowMapper;

    private final PreparedStatement insertPreparedStatement;
    private final PreparedStatement findByIdPreparedStatement;
    private final PreparedStatement findByDataPreparedStatement;

    public PropertyRepository(
            CassandraProperties properties,
            CqlSession session,
            Function<Row, Property> rowMapper
    ) {
        this.keyspace = CqlIdentifier.fromInternal(properties.getKeyspaceName());

        this.session = session;

        this.rowMapper = rowMapper;

        this.insertPreparedStatement = session.prepare(createInsertStatement());
        this.findByIdPreparedStatement = session.prepare(createFindByIdStatement());
        this.findByDataPreparedStatement = session.prepare(createFindByDataStatement());
    }

    public CompletionStage<Property> save(Property property) {
        BoundStatement bound = insertPreparedStatement.bind(
                property.getGroup(),
                property.getName(),
                property.getDate(),
                property.getValueString()
        );
        CompletionStage<AsyncResultSet> stage = session.executeAsync(bound);
        return stage.thenApply(rs -> property);
    }

    public CompletionStage<Optional<Property>> findById(String group, String name, Instant date) {
        BoundStatement bound = findByIdPreparedStatement.bind(group, name, date);
        CompletionStage<AsyncResultSet> stage = session.executeAsync(bound);
        return stage
                .thenApply(AsyncPagingIterable::one)
                .thenApply(Optional::ofNullable)
                .thenApply(optional -> optional.map(rowMapper));
    }

    public CompletionStage<Stream<Property>> findByData(
            String group,
            String name,
            Instant start,
            Instant end,
            long offset,
            long limit
    ) {
        BoundStatement bound = findByDataPreparedStatement.bind(group, name, start, end);
        CompletionStage<AsyncResultSet> stage = session.executeAsync(bound);
        return stage
                .thenCompose(first -> new RowCollector(first, offset, limit))
                .thenApply(rows -> rows.stream().map(rowMapper));
    }

    private SimpleStatement createInsertStatement() {
        return QueryBuilder.insertInto(keyspace, PROPERTY)
                .value(GROUP, bindMarker(GROUP))
                .value(NAME, bindMarker(NAME))
                .value(DATE, bindMarker(DATE))
                .value(VALUE_STRING, bindMarker(VALUE_STRING))
                .build();
    }

    private SimpleStatement createFindByIdStatement() {
        return QueryBuilder.selectFrom(keyspace, PROPERTY)
                .columns(
                        GROUP, NAME, DATE, VALUE_STRING
                )
                .where(column(GROUP).isEqualTo(bindMarker(GROUP)))
                .where(column(NAME).isEqualTo(bindMarker(NAME)))
                .where(column(DATE).isEqualTo(bindMarker(DATE)))
                .build();
    }

    private SimpleStatement createFindByDataStatement() {
        return QueryBuilder.selectFrom(keyspace, PROPERTY)
                .columns(
                        GROUP, NAME, DATE, VALUE_STRING
                )
                .where(
                        Relation.column(GROUP).isEqualTo(bindMarker(GROUP)),
                        Relation.column(NAME).isEqualTo(bindMarker(NAME)),
                        Relation.column(DATE).isGreaterThanOrEqualTo(bindMarker(START)),
                        Relation.column(DATE).isLessThan(bindMarker(END)))
                .build();
    }
}
