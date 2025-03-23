package io.rdlab.scylladb.fun.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;

@Component
public class StringToInstantConverter implements Converter<String, Instant> {

    private static final DateTimeFormatter PARSER =
            new DateTimeFormatterBuilder()
                    .appendValue(YEAR_OF_ERA, 4)
                    .optionalStart()
                    .appendValue(MONTH_OF_YEAR, 2)
                    .optionalStart()
                    .appendValue(DAY_OF_MONTH, 2)
                    .optionalStart()
                    .appendValue(HOUR_OF_DAY, 2)
                    .optionalStart()
                    .appendValue(MINUTE_OF_HOUR, 2)
                    .optionalStart()
                    .appendValue(SECOND_OF_MINUTE, 2)
                    .optionalStart()
                    .appendValue(MILLI_OF_SECOND, 3)
                    .optionalEnd()
                    .optionalEnd()
                    .optionalEnd()
                    .optionalEnd()
                    .optionalEnd()
                    .optionalEnd()
                    .parseDefaulting(MONTH_OF_YEAR, 1)
                    .parseDefaulting(DAY_OF_MONTH, 1)
                    .parseDefaulting(HOUR_OF_DAY, 0)
                    .parseDefaulting(MINUTE_OF_HOUR, 0)
                    .parseDefaulting(SECOND_OF_MINUTE, 0)
                    .parseDefaulting(MILLI_OF_SECOND, 0)
                    .toFormatter()
                    .withZone(UTC);

    @Override
    public Instant convert(@NonNull String source) {
        TemporalAccessor parsed = PARSER.parse(source);
        return Instant.from(parsed);
    }
}

