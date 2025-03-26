# io-rdlab-scylladb-fun
There are examples of Cassandra/ScyllaDB with Spring Boot 3.

## Functions. UDF/UDA
There is example of UDA for calculating most common value in text column type.
This example use custom type based on "map" and "{}" for INITCOND.

```
SELECT most_common_text(value_string) FROM dt.property WHERE group=:group AND name=:name AND date>=:start AND date<:end
```

* See: CassandraPreConstructSessionEntitiesInitializer
* See: [Scylladb. Functions](https://opensource.docs.scylladb.com/stable/cql/functions.html)

### Example

```lua
function accumulate(storage, val)
    if storage == nil then
        storage = {}
    end
    if val == nil then
        return storage
    end
    if storage[val] == nil then
        storage[val] = 1
    else
        storage[val] = storage[val] + 1
    end
    return storage
end

function calculate(storage)
    if storage == nil 
    then
        return nil
    end
    local value = nil
    local count = 0
    for v, c in pairs(storage) do
        if c > count then
            value = v
            count = c
        end
    end
    return value
end

function most_common(data)
    if data == nil 
    then
        return nil
    end
    local storage = {}
    for k, v in pairs(data) do
        storage = accumulate(storage, v)
    end
    return calculate(storage)
end    

function most_common_test() 
    local samples = {'data_1', 'data_2', 'data_2', 'data_3', 'data_5'}
    local result = most_common(samples)
    print(result)
end

most_common_test() 
```

```
CREATE OR REPLACE FUNCTION most_common_text_accumulate(storage _type_, val text)
RETURNS NULL ON NULL INPUT
RETURNS _type_
LANGUAGE lua
AS $$
...
$$;
```

```
CREATE OR REPLACE FUNCTION most_common_text_calculate(storage _type_)
RETURNS NULL ON NULL INPUT
RETURNS text
LANGUAGE lua AS $$
...
$$;
```

```
CREATE OR REPLACE AGGREGATE most_common_text(text)
   SFUNC most_common_text_accumulate
   STYPE _type_
   FINALFUNC most_common_text_calculate
   INITCOND _default_;
```

## Links
* [Accessing Data with Cassandra](https://spring.io/guides/gs/accessing-data-cassandra)
* [Scylladb. Docs](https://opensource.docs.scylladb.com/stable/getting-started/index.html)
* [Datastax. Docs](https://docs.datastax.com/en/developer/java-driver/4.17/manual/core/index.html)
* [Datastax. Cassandra/Scylla examples](https://github.com/datastax/cassandra-reactive-demo/tree/master)
* [Scylladb. Sources](https://github.com/scylladb/scylladb)
* [Scylladb. Functions](https://opensource.docs.scylladb.com/stable/cql/functions.html)
* [Scylladb. Video-streaming-app](https://www.scylladb.com/2024/01/09/build-a-low-latency-video-streaming-app/)
* [Scylladb. Aggregates](https://www.scylladb.com/2023/06/20/how-scylladb-distributed-aggregates-reduce-query-execution-time-up-to-20x/)
