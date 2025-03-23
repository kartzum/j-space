package io.rdlab.scylladb.fun.service;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

public interface CassandraPreConstructSessionInitializer {
    void execute(CqlSession session, CqlIdentifier keyspace);
}
