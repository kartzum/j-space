package io.rdlab.scylladb.fun.service;

import io.rdlab.scylladb.fun.model.Property;
import io.rdlab.scylladb.fun.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@Service
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public CompletionStage<Property> save(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public CompletionStage<Optional<Property>> findById(String group, String name, Instant date) {
        return propertyRepository.findById(group, name, date);
    }

    @Override
    public CompletionStage<Stream<Property>> findByData(
            String group, String name, Instant start, Instant end, long offset, long limit
    ) {
        return propertyRepository.findByData(group, name, start, end, offset, limit);
    }

    @Override
    public CompletionStage<Optional<String>> mostCommonText(
            String group, String name, Instant start, Instant end
    ) {
        return propertyRepository.mostCommonText(group, name, start, end);
    }

    @Override
    public CompletionStage<Optional<Long>> countAllByData(String group, String name, Instant start, Instant end) {
        return propertyRepository.countAllByData(group, name, start, end);
    }
}
