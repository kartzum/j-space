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
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import io.rdlab.scylladb.fun.model.Property;
import io.rdlab.scylladb.fun.service.Metrics;
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
    private final PreparedStatement mostCommonTextPreparedStatement;

    private final Metrics metrics;

    public PropertyRepository(
            CassandraProperties properties,
            CqlSession session,
            Function<Row, Property> rowMapper,
            Metrics metrics
    ) {
        this.keyspace = CqlIdentifier.fromInternal(properties.getKeyspaceName());

        this.session = session;

        this.rowMapper = rowMapper;

        this.insertPreparedStatement = session.prepare(createInsertStatement());
        this.findByIdPreparedStatement = session.prepare(createFindByIdStatement());
        this.findByDataPreparedStatement = session.prepare(createFindByDataStatement());
        this.mostCommonTextPreparedStatement = session.prepare(createMostCommonTextStatement());

        this.metrics = metrics;
    }

    public CompletionStage<Property> save(Property property) {
        BoundStatement bound = insertPreparedStatement.bind(
                property.getGroup(),
                property.getName(),
                property.getDate(),
                property.getValueString()
        );
        long startTime = Instant.now().toEpochMilli();
        CompletionStage<AsyncResultSet> stage = session.executeAsync(bound);
        return stage.thenApply(rs -> property)
                .whenComplete((propertyOpt, exception) -> {
                    if (exception == null) {
                        metrics.propertySaveTimerRegister(startTime);
                    } else {
                        metrics.propertySaveWithErrorTimerRegister(startTime);
                    }
                });
    }

    public CompletionStage<Optional<Property>> findById(String group, String name, Instant date) {
        long startTime = Instant.now().toEpochMilli();
        BoundStatement bound = findByIdPreparedStatement.bind(group, name, date);
        CompletionStage<AsyncResultSet> stage = session.executeAsync(bound);
        return stage
                .thenApply(AsyncPagingIterable::one)
                .thenApply(Optional::ofNullable)
                .thenApply(optional -> optional.map(rowMapper))
                .whenComplete((propertyOpt, exception) -> {
                    if (exception == null) {
                        metrics.propertyFindByIdTimerRegister(startTime);
                    } else {
                        metrics.propertyFindByIdWithErrorTimerRegister(startTime);
                    }
                });
    }

    public CompletionStage<Stream<Property>> findByData(
            String group,
            String name,
            Instant start,
            Instant end,
            long offset,
            long limit
    ) {
        long startTime = Instant.now().toEpochMilli();
        BoundStatement bound = findByDataPreparedStatement.bind(group, name, start, end);
        CompletionStage<AsyncResultSet> stage = session.executeAsync(bound);
        return stage
                .thenCompose(first -> new RowCollector(first, offset, limit))
                .thenApply(rows -> rows.stream().map(rowMapper))
                .whenComplete((propertyOpt, exception) -> {
                    if (exception == null) {
                        metrics.propertyFindByDataTimerRegister(startTime);
                    } else {
                        metrics.propertyFindByDataWithErrorTimerRegister(startTime);
                    }
                });
    }

    public CompletionStage<Optional<String>> mostCommonText(
            String group,
            String name,
            Instant start,
            Instant end
    ) {
        long startTime = Instant.now().toEpochMilli();
        BoundStatement bound = mostCommonTextPreparedStatement.bind(group, name, start, end);
        CompletionStage<AsyncResultSet> stage = session.executeAsync(bound);
        return stage
                .thenApply(AsyncPagingIterable::one)
                .thenApply(Optional::ofNullable)
                .thenApply(this::mostCommonExtractFunction)
                .whenComplete((propertyOpt, exception) -> {
                    if (exception == null) {
                        metrics.propertyMostCommonTextTimerRegister(startTime);
                    } else {
                        metrics.propertyMostCommonTextWithErrorTimerRegister(startTime);
                    }
                });
    }

    private Optional<String> mostCommonExtractFunction(Optional<Row> row) {
        return row.map(r -> r.getString(0));
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

    private SimpleStatement createMostCommonTextStatement() {
        return QueryBuilder.selectFrom(keyspace, PROPERTY)
                .function("most_common_text", Selector.column(VALUE_STRING))
                .where(
                        Relation.column(GROUP).isEqualTo(bindMarker(GROUP)),
                        Relation.column(NAME).isEqualTo(bindMarker(NAME)),
                        Relation.column(DATE).isGreaterThanOrEqualTo(bindMarker(START)),
                        Relation.column(DATE).isLessThan(bindMarker(END)))
                .build();
    }
}
