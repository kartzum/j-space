package io.rdlab.scylladb.fun.service;

import io.rdlab.scylladb.fun.model.Property;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface PropertyService {
    CompletionStage<Property> save(Property property);

    CompletionStage<Optional<Property>> findById(String group, String name, Instant date);

    CompletionStage<Stream<Property>> findByData(
            String group,
            String name,
            Instant start,
            Instant end,
            long offset,
            long limit
    );

    CompletionStage<Optional<String>> mostCommonText(
            String group,
            String name,
            Instant start,
            Instant end
    );
}
