package io.rdlab.scylladb.fun.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.time.Instant;

@JsonComponent
public class InstantDeserializer extends StdScalarDeserializer<Instant> {

    private final Converter<String, Instant> converter;

    public InstantDeserializer(Converter<String, Instant> converter) {
        super(Instant.class);
        this.converter = converter;
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            String text = p.readValueAs(String.class);
            return text == null ? null : converter.convert(text);
        } catch (Exception e) {
            throw new JsonParseException(p, "Could not parse node as instant", e);
        }
    }
}

