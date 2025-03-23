# io-rdlab-scylladb-fun
There are examples of Cassandra/ScyllaDB with Spring Boot 3.

## Functions. UDF/UDA
There is example of UDA for calculating most frequency value in text column type.
This example use custom type based on "map" and "{}" for INITCOND.

```
SELECT max_frequency_text(value_string) FROM dt.property WHERE group=:group AND name=:name AND date>=:start AND date<:end
```

* See: CassandraPreConstructSessionEntitiesInitializer
* See: [Scylladb. Functions](https://opensource.docs.scylladb.com/stable/cql/functions.html)

## Links
* [Accessing Data with Cassandra](https://spring.io/guides/gs/accessing-data-cassandra)
* [Scylladb. Docs](https://opensource.docs.scylladb.com/stable/getting-started/index.html)
* [Datastax. Docs](https://docs.datastax.com/en/developer/java-driver/4.17/manual/core/index.html)
* [Datastax. Cassandra/Scylla examples](https://github.com/datastax/cassandra-reactive-demo/tree/master)
* [Scylladb. Sources](https://github.com/scylladb/scylladb)
* [Scylladb. Functions](https://opensource.docs.scylladb.com/stable/cql/functions.html)
* [Scylladb. Video-streaming-app](https://www.scylladb.com/2024/01/09/build-a-low-latency-video-streaming-app/)
* [Scylladb. Aggregates](https://www.scylladb.com/2023/06/20/how-scylladb-distributed-aggregates-reduce-query-execution-time-up-to-20x/)
