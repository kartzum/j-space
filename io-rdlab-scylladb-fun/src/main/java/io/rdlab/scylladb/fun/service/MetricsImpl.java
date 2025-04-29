package io.rdlab.scylladb.fun.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class MetricsImpl implements Metrics {
    private static final String EXCEPTION_TAG_NAME = "exception";

    private final Timer propertySaveTimer;
    private final Timer propertySaveWithErrorTimer;

    private final Timer propertyFindByIdTimer;
    private final Timer propertyFindByIdWithErrorTimer;

    private final Timer propertyFindByDataTimer;
    private final Timer propertyFindByDataWithErrorTimer;

    private final Timer propertyMostCommonTextTimer;
    private final Timer propertyMostCommonTextWithErrorTimer;

    public MetricsImpl(MeterRegistry meterRegistry) {
        this.propertySaveTimer = Timer.builder("propertySave")
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertySaveWithErrorTimer = Timer.builder("propertySave")
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);
        this.propertyFindByIdTimer = Timer.builder("propertyFindById")
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertyFindByIdWithErrorTimer = Timer.builder("propertyFindById")
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);
        this.propertyFindByDataTimer = Timer.builder("propertyFindByData")
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertyFindByDataWithErrorTimer = Timer.builder("propertyFindByData")
                .tag(EXCEPTION_TAG_NAME, "Error")
                .register(meterRegistry);
        this.propertyMostCommonTextTimer = Timer.builder("propertyMostCommonText")
                .tag(EXCEPTION_TAG_NAME, "none")
                .register(meterRegistry);
        this.propertyMostCommonTextWithErrorTimer = Timer.builder("propertyMostCommonText")
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
