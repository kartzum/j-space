package io.rdlab.scylladb.fun.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.time.Instant;

@JsonComponent
public class InstantSerializer extends StdSerializer<Instant> {

    private final Converter<Instant, String> converter;

    public InstantSerializer(Converter<Instant, String> converter) {
        super(Instant.class);
        this.converter = converter;
    }

    @Override
    public void serialize(Instant instant, JsonGenerator gen, SerializerProvider serializerProvider)
            throws IOException {
        try {
            if (instant == null) {
                gen.writeNull();
            } else {
                gen.writeString(converter.convert(instant));
            }
        } catch (Exception e) {
            throw new JsonGenerationException("Could not serialize instant: " + instant, e, gen);
        }
    }
}
