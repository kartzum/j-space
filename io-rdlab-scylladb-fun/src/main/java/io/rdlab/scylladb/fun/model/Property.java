package io.rdlab.scylladb.fun.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Objects;

public class Property {
    private final String group;
    private final String name;
    private final Instant date;
    private final String valueString;

    @JsonCreator
    public Property(
            @NonNull @JsonProperty("group") String group,
            @NonNull @JsonProperty("name") String name,
            @NonNull @JsonProperty("date") Instant date,
            @JsonProperty("valueString") String valueString
    ) {
        this.group = group;
        this.name = name;
        this.date = date;
        this.valueString = valueString;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public Instant getDate() {
        return date;
    }

    public String getValueString() {
        return valueString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(group, property.group) && Objects.equals(name, property.name) &&
                Objects.equals(date, property.date) && Objects.equals(valueString, property.valueString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, name, date, valueString);
    }
}
