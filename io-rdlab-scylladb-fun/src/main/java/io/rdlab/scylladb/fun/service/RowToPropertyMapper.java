package io.rdlab.scylladb.fun.service;

import com.datastax.oss.driver.api.core.cql.Row;
import io.rdlab.scylladb.fun.model.Property;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

import static io.rdlab.scylladb.fun.constants.PropertyConstants.DATE;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.GROUP;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.NAME;
import static io.rdlab.scylladb.fun.constants.PropertyConstants.VALUE_STRING;

@Component
public class RowToPropertyMapper implements Function<Row, Property> {
    @Override
    public Property apply(Row row) {
        String group = Objects.requireNonNull(row.getString(GROUP), "column group cannot be null");
        String name = Objects.requireNonNull(row.getString(NAME), "column name cannot be null");
        Instant date = Objects.requireNonNull(row.getInstant(DATE), "column date cannot be null");
        String valueString = null;
        if (row.getColumnDefinitions().contains(VALUE_STRING)) {
            valueString = row.get(VALUE_STRING, String.class);
        }
        return new Property(group, name, date, valueString);
    }
}
