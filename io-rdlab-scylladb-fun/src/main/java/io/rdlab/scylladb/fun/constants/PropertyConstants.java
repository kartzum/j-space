package io.rdlab.scylladb.fun.constants;

import com.datastax.oss.driver.api.core.CqlIdentifier;

public class PropertyConstants {
    public static final CqlIdentifier PROPERTY = CqlIdentifier.fromCql("property");

    public static final CqlIdentifier GROUP = CqlIdentifier.fromCql("group");
    public static final CqlIdentifier NAME = CqlIdentifier.fromCql("name");
    public static final CqlIdentifier DATE = CqlIdentifier.fromCql("date");
    public static final CqlIdentifier VALUE_STRING = CqlIdentifier.fromCql("value_string");

    public static final CqlIdentifier START = CqlIdentifier.fromCql("start");
    public static final CqlIdentifier END = CqlIdentifier.fromCql("end");
}
