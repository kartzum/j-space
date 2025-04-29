package io.rdlab.scylladb.fun.service;

public interface Metrics {
    void propertySaveTimerRegister(long start);

    void propertySaveWithErrorTimerRegister(long start);

    void propertyFindByIdTimerRegister(long start);

    void propertyFindByIdWithErrorTimerRegister(long start);

    void propertyFindByDataTimerRegister(long start);

    void propertyFindByDataWithErrorTimerRegister(long start);

    void propertyMostCommonTextTimerRegister(long start);

    void propertyMostCommonTextWithErrorTimerRegister(long start);
}
