package io.rdlab.scylladb.fun.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;

@Component
public class InstantToStringConverter implements Converter<Instant, String> {

    private static final DateTimeFormatter FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendValue(YEAR_OF_ERA, 4)
                    .appendValue(MONTH_OF_YEAR, 2)
                    .appendValue(DAY_OF_MONTH, 2)
                    .appendValue(HOUR_OF_DAY, 2)
                    .appendValue(MINUTE_OF_HOUR, 2)
                    .appendValue(SECOND_OF_MINUTE, 2)
                    .appendValue(MILLI_OF_SECOND, 3)
                    .toFormatter();

    @Override
    public String convert(@NonNull Instant source) {
        ZonedDateTime zonedDateTime = source.atZone(UTC);
        return FORMATTER.format(zonedDateTime);
    }
}
