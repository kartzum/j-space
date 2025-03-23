package io.rdlab.scylladb.fun.service;

import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RowCollector extends CompletableFuture<List<Row>> {

    final List<Row> rows = new ArrayList<>();
    long offset;
    long limit;

    public RowCollector(AsyncResultSet first, long offset, long limit) {
        this.offset = offset;
        this.limit = limit;
        consumePage(first);
    }

    public void consumePage(AsyncResultSet page) {
        for (Row row : page.currentPage()) {
            if (offset > 0) {
                offset--;
            } else if (limit > 0) {
                rows.add(row);
                limit--;
            }
        }
        if (page.hasMorePages() && limit > 0) {
            page.fetchNextPage().thenAccept(this::consumePage);
        } else {
            complete(rows);
        }
    }
}

