package io.rdlab.scylladb.fun.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class MetricsImpl implements Metrics {
    private static final String EXCEPTION_TAG_NAME = "exception";

    private static final Duration[] DURATIONS = new Duration[]{
            Duration.ofMillis(1),
            Duration.ofMillis(5),
            Duration.ofMillis(10),
            Duration.ofMillis(100),
            Duration.ofMillis(500),
            Duration.ofMillis(1000),
            Duration.ofMillis(10000),
            Duration.ofMillis(100000),
    };

    private final Timer propertySaveTimer;
    private final Timer propertySaveWithErrorTimer;

    private final Timer propertyFindByIdTimer;
    private final Timer propertyFindByIdWithErrorTimer;

    private final Counter propertyFindByIdCounter;
    private final Counter propertyFindByIdWithErrorCounter;

    private final Timer propertyFindByDataTimer;
    private final Timer propertyFindByDataWithErrorTimer;

    private final Timer propertyMostCommonTextTimer;
    private final Timer propertyMostCommonTextWithErrorTimer;

    public MetricsImpl(MeterRegistry meterRegistry) {
        this.propertySaveTimer = Timer.builder("propertySaveTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertySaveWithErrorTimer = Timer.builder("propertySaveTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);

        this.propertyFindByIdTimer = Timer.builder("propertyFindByIdTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertyFindByIdWithErrorTimer = Timer.builder("propertyFindByIdTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);
        this.propertyFindByIdCounter = Counter.builder("propertyFindByIdCounter")
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertyFindByIdWithErrorCounter = Counter.builder("propertyFindByIdCounter")
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);

        this.propertyFindByDataTimer = Timer.builder("propertyFindByDataTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertyFindByDataWithErrorTimer = Timer.builder("propertyFindByDataTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);

        this.propertyMostCommonTextTimer = Timer.builder("propertyMostCommonTextTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertyMostCommonTextWithErrorTimer = Timer.builder("propertyMostCommonTextTimer")
                .serviceLevelObjectives(DURATIONS)
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);
    }

    @Override
    public void propertySaveTimerRegister(long start) {
        propertySaveTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }

    @Override
    public void propertySaveWithErrorTimerRegister(long start) {
        propertySaveWithErrorTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }

    @Override
    public void propertyFindByIdTimerRegister(long start) {
        propertyFindByIdTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }

    @Override
    public void propertyFindByIdWithErrorTimerRegister(long start) {
        propertyFindByIdWithErrorTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }

    @Override
    public void propertyFindByIdCounterRegister() {
        propertyFindByIdCounter.increment();
    }

    @Override
    public void propertyFindByIdWithErrorCounterRegister() {
        propertyFindByIdWithErrorCounter.increment();
    }

    @Override
    public void propertyFindByDataTimerRegister(long start) {
        propertyFindByDataTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }

    @Override
    public void propertyFindByDataWithErrorTimerRegister(long start) {
        propertyFindByDataWithErrorTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }

    @Override
    public void propertyMostCommonTextTimerRegister(long start) {
        propertyMostCommonTextTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }

    @Override
    public void propertyMostCommonTextWithErrorTimerRegister(long start) {
        propertyMostCommonTextWithErrorTimer.record(Instant.now().toEpochMilli() - start, TimeUnit.MILLISECONDS);
    }
}
