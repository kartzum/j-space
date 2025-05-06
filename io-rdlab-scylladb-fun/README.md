# io-rdlab-scylladb-fun
There are examples of Cassandra/ScyllaDB with Spring Boot 3.

## Local run and samples

Add "property".
```
curl -l 'localhost:8080/api/v1/property' \
--header 'Content-Type: application/json' \
--data-raw '{"group": "g", "name": "a", "date": "20250101000000000", "valueString": "data_1"}'
```

Get/find... "property" and.
```
curl -l 'localhost:8080/api/v1/property/g/a/20250101000000000'
```

```
curl -l 'localhost:8080/api/v1/property/find?group=g&name=a&start=20250101000000000&end=20250101000000900&offset=0&limit=10'
```

```
curl -l 'localhost:8080/api/v1/property/count-all?group=g&name=a_9&start=20250101000000000&end=20250101000000900'
```

## Cassandra/ScyllaDB. Async
* https://github.com/apache/cassandra-java-driver/blob/4.x/manual/core/async/README.md

## Cassandra/ScyllaDB. Metrics

* https://github.com/apache/cassandra-java-driver/blob/4.x/manual/core/metrics/README.md

Get info about 'findById'.
```
curl -l 'localhost:8080/actuator/prometheus' | grep property
```
Responses.
```
# HELP propertyFindByDataTimer_seconds  
# TYPE propertyFindByDataTimer_seconds summary
propertyFindByDataTimer_seconds_count{exception="Error"} 0
propertyFindByDataTimer_seconds_sum{exception="Error"} 0.0
propertyFindByDataTimer_seconds_count{exception="none"} 0
propertyFindByDataTimer_seconds_sum{exception="none"} 0.0
# HELP propertyFindByDataTimer_seconds_max  
# TYPE propertyFindByDataTimer_seconds_max gauge
propertyFindByDataTimer_seconds_max{exception="Error"} 0.0
propertyFindByDataTimer_seconds_max{exception="none"} 0.0
# HELP propertyFindByIdCounter_total  
# TYPE propertyFindByIdCounter_total counter
propertyFindByIdCounter_total{exception="Error"} 0.0
propertyFindByIdCounter_total{exception="none"} 0.0
# HELP propertyFindByIdTimer_seconds  
# TYPE propertyFindByIdTimer_seconds summary
propertyFindByIdTimer_seconds_count{exception="Error"} 0
propertyFindByIdTimer_seconds_sum{exception="Error"} 0.0
propertyFindByIdTimer_seconds_count{exception="none"} 0
propertyFindByIdTimer_seconds_sum{exception="none"} 0.0
# HELP propertyFindByIdTimer_seconds_max  
# TYPE propertyFindByIdTimer_seconds_max gauge
propertyFindByIdTimer_seconds_max{exception="Error"} 0.0
propertyFindByIdTimer_seconds_max{exception="none"} 0.0
...
```
```
...
# HELP propertyFindByIdCounter_total  
# TYPE propertyFindByIdCounter_total counter
propertyFindByIdCounter_total{exception="Error"} 0.0
propertyFindByIdCounter_total{exception="none"} 7.0
# HELP propertyFindByIdTimer_seconds  
# TYPE propertyFindByIdTimer_seconds summary
propertyFindByIdTimer_seconds_count{exception="Error"} 0
propertyFindByIdTimer_seconds_sum{exception="Error"} 0.0
propertyFindByIdTimer_seconds_count{exception="none"} 7
propertyFindByIdTimer_seconds_sum{exception="none"} 0.034
...
```
Rate and others.
```
rate(propertyFindByIdTimer_seconds_count {exception="none"}[1m])

rate(propertyFindByDataTimer_seconds_count {exception="none"}[1m])
rate(propertyFindByDataTimer_seconds_count {exception="Error"}[1m])

rate(propertySaveTimer_seconds_count {exception="none"}[1m])

rate(propertyMostCommonTextTimer_seconds_count {exception="none"}[1m])
rate(propertyMostCommonTextTimer_seconds_count {exception="Error"}[1m])
```

```
rate(cassandra_session_cql_requests_seconds_count[1m])

cassandra_nodes_pool_open_connections

cassandra_nodes_pool_available_streams
```

```
process_cpu_usage
```

## Load testing
See:
* https://jmeter.apache.org/index.html
* https://jmeter.apache.org/usermanual/build-web-test-plan.html

Plans:
* fun-property-put-1.jmx
* fun-property-find-1.jmx
* fun-property-most-common-text-1.jmx

## Cassandra/ScyllaDB. Performance
See:
* https://github.com/apache/cassandra-java-driver/blob/4.x/manual/core/performance/README.md
* https://github.com/apache/cassandra-java-driver/tree/4.x/manual/core/pooling#tuning
* https://docs.datastax.com/en/developer/java-driver/4.4/manual/core/configuration/reference/index.html

## Cassandra/ScyllaDB. Functions. UDF/UDA
There is example of UDA for calculating most common value in text column type.
This example use custom type based on "map" and "{}" for INITCOND.

```
SELECT most_common_text(value_string) FROM dt.property WHERE group=:group AND name=:name AND date>=:start AND date<:end
```

* See: CassandraPreConstructSessionEntitiesInitializer
* See: [Scylladb. Functions](https://opensource.docs.scylladb.com/stable/cql/functions.html)

### Example

```cql
CREATE KEYSPACE propertyexp WITH replication = {'class': 'NetworkTopologyStrategy', 'replication_factor' : 1};
```

```cql
CREATE TABLE IF NOT EXISTS propertyexp.property (
group text,
name text,
date timestamp,
value_string text,
PRIMARY KEY((group,name),date)) WITH CLUSTERING ORDER BY (date DESC);
```

```cql
INSERT INTO propertyexp.property (group, name, date, value_string) VALUES ('g', 'a', '2025-03-11 03:15:13', 'data_1');
INSERT INTO propertyexp.property (group, name, date, value_string) VALUES ('g', 'a', '2025-03-11 03:15:14', 'data_2');
INSERT INTO propertyexp.property (group, name, date, value_string) VALUES ('g', 'a', '2025-03-11 03:15:15', 'data_2');
INSERT INTO propertyexp.property (group, name, date, value_string) VALUES ('g', 'a', '2025-03-11 03:15:16', 'data_3');
INSERT INTO propertyexp.property (group, name, date, value_string) VALUES ('g', 'a', '2025-03-11 03:15:17', 'data_5');
```

```
 group | name | date                            | value_string
-------+------+---------------------------------+--------------
     g |    a | 2025-03-11 00:15:17.000000+0000 |       data_5
     g |    a | 2025-03-11 00:15:16.000000+0000 |       data_3
     g |    a | 2025-03-11 00:15:15.000000+0000 |       data_2
     g |    a | 2025-03-11 00:15:14.000000+0000 |       data_2
     g |    a | 2025-03-11 00:15:13.000000+0000 |       data_1
```

```cql
SELECT propertyexp.most_common_text(value_string) 
FROM propertyexp.property 
WHERE date >= '2025-03-11 00:00:00' AND date < '2025-03-11 23:00:00';
```

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

```cql
CREATE OR REPLACE FUNCTION most_common_text_accumulate(storage _type_, val text)
RETURNS NULL ON NULL INPUT
RETURNS _type_
LANGUAGE lua
AS $$
...
$$;
```

```cql
CREATE OR REPLACE FUNCTION most_common_text_calculate(storage _type_)
RETURNS NULL ON NULL INPUT
RETURNS text
LANGUAGE lua AS $$
...
$$;
```

```cql
CREATE OR REPLACE AGGREGATE most_common_text(text)
   SFUNC most_common_text_accumulate
   STYPE _type_
   FINALFUNC most_common_text_calculate
   INITCOND _default_;
```

**Based on 'map<text, bigint>'**

```cql
CREATE OR REPLACE FUNCTION most_common_text_accumulate_map(storage map<text, bigint>, val text)
RETURNS NULL ON NULL INPUT
RETURNS map<text, bigint>
LANGUAGE lua
AS $$
  if storage == null then
    storage = {}
  end
  if storage[val] == null then
    storage[val] = 1
  else
    storage[val] = storage[val] + 1
  end
return storage
$$;

CREATE OR REPLACE FUNCTION most_common_text_calculate_map(storage map<text, bigint>)
RETURNS NULL ON NULL INPUT
RETURNS text
LANGUAGE lua AS $$
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
$$;
```

```
1) null
CREATE OR REPLACE AGGREGATE most_common_text_map(text)
   SFUNC most_common_text_accumulate_map
   STYPE map<text, bigint>
   FINALFUNC most_common_text_calculate_map
   INITCOND null;

CREATE OR REPLACE AGGREGATE most_common_text_map(text)
               ...    SFUNC most_common_text_accumulate_map
               ...    STYPE map<text, bigint>
               ...    FINALFUNC most_common_text_calculate_map
               ...    INITCOND null;
ServerError: <Error from server: code=0000 [Server error] message="marshaling error: read_simple - not enough bytes (expected 4, got 0) Backtrace: 0x415a30e ...libreloc/libc.so.6+0x100352
   --------
   seastar::lambda_task<seastar::execution_stage::flush()::$_5>">

2) NULL
CREATE OR REPLACE AGGREGATE most_common_text_map(text)
   SFUNC most_common_text_accumulate_map
   STYPE map<text, bigint>
   FINALFUNC most_common_text_calculate_map
   INITCOND NULL;

CREATE OR REPLACE AGGREGATE most_common_text_map(text)
               ...    SFUNC most_common_text_accumulate_map
               ...    STYPE map<text, bigint>
               ...    FINALFUNC most_common_text_calculate_map
               ...    INITCOND NULL;
ServerError: <Error from server: code=0000 [Server error] message="marshaling error: read_simple - not enough bytes (expected 4, got 0) Backtrace: 0x415a30e .../libreloc/libc.so.6+0x100352
   --------
   seastar::lambda_task<seastar::execution_stage::flush()::$_5>">

3) Without INITCOND
CREATE OR REPLACE AGGREGATE most_common_text_map(text)
   SFUNC most_common_text_accumulate_map
   STYPE map<text, bigint>
   FINALFUNC most_common_text_calculate_map;

CREATE OR REPLACE AGGREGATE most_common_text_map(text)
               ...    SFUNC most_common_text_accumulate_map
               ...    STYPE map<text, bigint>
               ...    FINALFUNC most_common_text_calculate_map;
ServerError: <Error from server: code=0000 [Server error] message="conjunctions are not yet reachable via term_raw_expr::prepare() Backtrace: 0x415a30e .../libreloc/libc.so.6+0x100352
   --------
   seastar::lambda_task<seastar::execution_stage::flush()::$_5>">

4) nil
CREATE OR REPLACE AGGREGATE most_common_text_map(text)
   SFUNC most_common_text_accumulate_map
   STYPE map<text, bigint>
   FINALFUNC most_common_text_calculate_map
   INITCOND nil;

CREATE OR REPLACE AGGREGATE most_common_text_map(text)
               ...    SFUNC most_common_text_accumulate_map
               ...    STYPE map<text, bigint>
               ...    FINALFUNC most_common_text_calculate_map
               ...    INITCOND nil;
SyntaxException: <Error from server: code=2000 [Syntax error in CQL query] message="line 5:15 no viable alternative at input ';'">

5) {}
CREATE OR REPLACE AGGREGATE most_common_text_map(text)
   SFUNC most_common_text_accumulate_map
   STYPE map<text, bigint>
   FINALFUNC most_common_text_calculate_map
   INITCOND {};

CREATE OR REPLACE AGGREGATE most_common_text_map(text)
               ...

SFUNC most_common_text_accumulate_map
               ...    STYPE map<text, bigint>
               ...    FINALFUNC most_common_text_calculate_map
               ...    INITCOND {};
ServerError: <Error from server: code=0000 [Server error] message="marshaling error: read_simple - not enough bytes (expected 4, got 0) Backtrace: 0x415a30e .../libreloc/libc.so.6+0x100352
   --------
   seastar::lambda_task<seastar::execution_stage::flush()::$_5>">
```

**Based on 'frozen map<text, bigint>'**

```cql
CREATE OR REPLACE FUNCTION most_common_text_accumulate_map_frozen(storage frozen<map<text, bigint>>, val text)
RETURNS NULL ON NULL INPUT
RETURNS map<text, bigint>
LANGUAGE lua
AS $$
  if storage == null then
    storage = {}
  end
  if storage[val] == null then
    storage[val] = 1
  else
    storage[val] = storage[val] + 1
  end
return storage
$$;

CREATE OR REPLACE FUNCTION most_common_text_calculate_map_frozen(storage frozen<map<text, bigint>>)
RETURNS NULL ON NULL INPUT
RETURNS text
LANGUAGE lua AS $$
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
$$;
```

```
1) null
CREATE OR REPLACE AGGREGATE most_common_text_map_frozen(text)
   SFUNC most_common_text_accumulate_map_frozen
   STYPE frozen<map<text, bigint>>
   FINALFUNC most_common_text_calculate_map_frozen
   INITCOND null;

CREATE OR REPLACE AGGREGATE most_common_text_map_frozen(text)
               ...    SFUNC most_common_text_accumulate_map_frozen
               ...    STYPE frozen<map<text, bigint>>
               ...    FINALFUNC most_common_text_calculate_map_frozen
               ...    INITCOND null;
ServerError: <Error from server: code=0000 [Server error] message="marshaling error: read_simple - not enough bytes (expected 4, got 0) Backtrace: 0x415a30e .../libreloc/libc.so.6+0x100352
   --------
   seastar::lambda_task<seastar::execution_stage::flush()::$_5>">

2) Without INITCOND
CREATE OR REPLACE AGGREGATE most_common_text_map_frozen(text)
   SFUNC most_common_text_accumulate_map_frozen
   STYPE frozen<map<text, bigint>>
   FINALFUNC most_common_text_calculate_map_frozen;

CREATE OR REPLACE AGGREGATE most_common_text_map_frozen(text)
               ...    SFUNC most_common_text_accumulate_map_frozen
               ...    STYPE frozen<map<text, bigint>>
               ...    FINALFUNC most_common_text_calculate_map_frozen;
ServerError: <Error from server: code=0000 [Server error] message="conjunctions are not yet reachable via term_raw_expr::prepare() Backtrace: 0x415a30e .../libreloc/libc.so.6+0x100352
   --------
   seastar::lambda_task<seastar::execution_stage::flush()::$_5>">

3) {}
CREATE OR REPLACE AGGREGATE most_common_text_map_frozen(text)
   SFUNC most_common_text_accumulate_map_frozen
   STYPE frozen<map<text, bigint>>
   FINALFUNC most_common_text_calculate_map_frozen
   INITCOND {};

CREATE OR REPLACE AGGREGATE most_common_text_map_frozen(text)
               ...    SFUNC most_common_text_accumulate_map_frozen
               ...    STYPE frozen<map<text, bigint>>
               ...    FINALFUNC most_common_text_calculate_map_frozen
               ...    INITCOND {};
```

```cql
SELECT propertyexp.most_common_text_map_frozen(value_string) FROM propertyexp.property WHERE date >= '2025-03-11 00:00:00' AND date < '2025-03-11 23:00:00';

SELECT propertyexp.most_common_text_map_frozen(value_string) FROM propertyexp.property WHERE date >= '2025-03-11 00:00:00' AND date < '2025-03-11 23:00:00';
InvalidRequest: Error from server: code=2200 [Invalid query] message="Unknown function 'propertyexp.most_common_text_map_frozen'"
```

**Based on 'custom type with map<bigint, bigint>'**

```cql
CREATE TYPE IF NOT EXISTS most_common_text_data_map ( text_data map<bigint, bigint> );
```

```cql
CREATE OR REPLACE FUNCTION most_common_text_accumulate_map_custom(storage most_common_text_data_map, val text)
RETURNS NULL ON NULL INPUT
RETURNS most_common_text_data_map
LANGUAGE lua
AS $$
    if storage == nil then
        storage = {}
    end
    if storage.text_data == nil then
        storage.text_data = {}
    end
    if val == nil then
        return storage
    end
    if storage.text_data[val] == nil then
        storage.text_data[val] = 1
    else
        storage.text_data[val] = storage.text_data[val] + 1
    end
    return storage
$$;

CREATE OR REPLACE FUNCTION most_common_text_calculate_map_custom(storage most_common_text_data_map)
RETURNS NULL ON NULL INPUT
RETURNS text
LANGUAGE lua AS $$
    if storage == nil or storage.text_data == nil then
        return nil
    end
    local value = nil
    local count = 0
    for v, c in pairs(storage.text_data) do
        if c > count then
            value = v
            count = c
        end
    end
    return value
$$;
```

```cql
CREATE OR REPLACE AGGREGATE most_common_text_map_custom(text)
   SFUNC most_common_text_accumulate_map_custom
   STYPE most_common_text_data_map
   FINALFUNC most_common_text_calculate_map_custom
   INITCOND {text_data: {}};
```

```
SELECT propertyexp.most_common_text_map_custom(value_string) FROM propertyexp.property WHERE date >= '2025-03-11 00:00:00' AND date < '2025-03-11 23:00:00';
InvalidRequest: Error from server: code=2200 [Invalid query] message="value is not a number"
```

**Based on 'custom type with frozen<map<bigint, bigint>>'**

```cql
CREATE TYPE IF NOT EXISTS most_common_text_data ( text_data frozen<map<bigint, bigint>> );
```

```cql
CREATE OR REPLACE FUNCTION most_common_text_accumulate(storage most_common_text_data, val text)
RETURNS NULL ON NULL INPUT
RETURNS most_common_text_data
LANGUAGE lua
AS $$
    if storage == nil then
        storage = {}
    end
    if storage.text_data == nil then
        storage.text_data = {}
    end
    if val == nil then
        return storage
    end
    if storage.text_data[val] == nil then
        storage.text_data[val] = 1
    else
        storage.text_data[val] = storage.text_data[val] + 1
    end
    return storage
$$;

CREATE OR REPLACE FUNCTION most_common_text_calculate(storage most_common_text_data)
RETURNS NULL ON NULL INPUT
RETURNS text
LANGUAGE lua AS $$
    if storage == nil or storage.text_data == nil then
        return nil
    end
    local value = nil
    local count = 0
    for v, c in pairs(storage.text_data) do
        if c > count then
            value = v
            count = c
        end
    end
    return value
$$;
```

```cql
CREATE OR REPLACE AGGREGATE most_common_text(text)
   SFUNC most_common_text_accumulate
   STYPE most_common_text_data
   FINALFUNC most_common_text_calculate
   INITCOND {text_data: {}};
```

```
SELECT propertyexp.most_common_text(value_string) FROM propertyexp.property WHERE date >= '2025-03-11 00:00:00' AND date < '2025-03-11 23:00:00';

 propertyexp.most_common_text(value_string)
--------------------------------------------
                                     data_2

(1 rows)
```

## Cassandra/ScyllaDB. Performance experiments
### find

* fun-property-put-1.jmx
* fun-property-find-1.jmx

```
a_0: 35
a_1: 52
a_2: 38
a_3: 46
a_4: 43
a_5: 46
a_6: 37
a_7: 44
a_8: 40
a_9: 40
```

**default**
```
advanced.netty.io-group.size: 0
advanced.connection.pool.local.size: 1
```
**1400/50**
```
{"timestamp":"2025-05-06T15:42:05.520","level":"ERROR","thread":"http-nio-8080-exec-301","logger":"o.a.c.c.C.[.[.[/].[dispatcherServlet]","message":"Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: com.datastax.oss.driver.api.core.AllNodesFailedException: All 1 node(s) tried for the query failed (showing first 1 nodes, use getAllErrors() for more): Node(endPoint=/127.0.0.1:9042, hostId=97eb30d1-e48b-423c-b8c9-673c55346133, hashCode=115a69e4): [com.datastax.oss.driver.api.core.NodeUnavailableException: No connection was available to Node(endPoint=/127.0.0.1:9042, hostId=97eb30d1-e48b-423c-b8c9-673c55346133, hashCode=115a69e4)]] with root cause","traceId":"","spanId":"","stackTrace":"com.datastax.oss.driver.api.core.AllNodesFailedException: All 1 node(s) tried for the query failed (showing first 1 nodes, use getAllErrors() for more): Node(endPoint=/127.0.0.1:9042, hostId=97eb30d1-e48b-423c-b8c9-673c55346133, hashCode=115a69e4): [com.datastax.oss.driver.api.core.NodeUnavailableException: No connection was available to Node(endPoint=/127.0.0.1:9042, hostId=97eb30d1-e48b-423c-b8c9-673c55346133, hashCode=115a69e4)]\n\tat com.datastax.oss.driver.api.core.AllNodesFailedException.fromErrors(AllNodesFailedException.java:57)\n\tat com.datastax.oss.driver.internal.core.cql.CqlRequestHandler.sendRequest(CqlRequestHandler.java:266)\n\tat com.datastax.oss.driver.internal.core.cql.CqlRequestHandler.onThrottleReady(CqlRequestHandler.java:197)\n\tat com.datastax.oss.driver.internal.core.session.throttling.PassThroughRequestThrottler.register(PassThroughRequestThrottler.java:54)\n\tat com.datastax.oss.driver.internal.core.cql.CqlRequestHandler.<init>(CqlRequestHandler.java:174)\n\tat com.datastax.oss.driver.internal.core.cql.CqlRequestAsyncProcessor.process(CqlRequestAsyncProcessor.java:46)\n\tat com.datastax.oss.driver.internal.core.cql.CqlRequestAsyncProcessor.process(CqlRequestAsyncProcessor.java:31)\n\tat com.datastax.oss.driver.internal.core.session.DefaultSession.execute(DefaultSession.java:234)\n\tat com.datastax.oss.driver.api.core.cql.AsyncCqlSession.executeAsync(AsyncCqlSession.java:44)\n\tat io.rdlab.scylladb.fun.repository.PropertyRepository.findByData(PropertyRepository.java:118)\n\tat java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:565)\n\tat org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:359)\n\tat org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\n\tat org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\n\tat org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:138)\n\tat org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\n\tat org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\n\tat io.rdlab.scylladb.fun.repository.PropertyRepository$$SpringCGLIB$$0.findByData(<generated>)\n\tat io.rdlab.scylladb.fun.service.PropertyServiceImpl.findByData(PropertyServiceImpl.java:34)\n\tat io.rdlab.scylladb.fun.controller.PropertyController.find(PropertyController.java:58)\n\tat java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:565)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\n\tat org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:986)\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:891)\n\tat org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\n\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\n\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\n\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\n\tat org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903)\n\tat jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564)\n\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\n\tat jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\n\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\n\tat org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\n\tat org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\n\tat org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:114)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\n\tat org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\n\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\n\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\n\tat org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\n\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\n\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\n\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\n\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\n\tat org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\n\tat org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\n\tat org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\n\tat org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1740)\n\tat org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\n\tat org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\n\tat org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\n\tat org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\n\tat java.base/java.lang.Thread.run(Thread.java:1447)\n\tSuppressed: com.datastax.oss.driver.api.core.NodeUnavailableException: No connection was available to Node(endPoint=/127.0.0.1:9042, hostId=97eb30d1-e48b-423c-b8c9-673c55346133, hashCode=115a69e4)\n\t\tat com.datastax.oss.driver.internal.core.cql.CqlRequestHandler.sendRequest(CqlRequestHandler.java:258)\n\t\t... 71 common frames omitted\n"}
```
**1400/50_10/8**

## Links
* [Accessing Data with Cassandra](https://spring.io/guides/gs/accessing-data-cassandra)
* [Scylladb. Docs](https://opensource.docs.scylladb.com/stable/getting-started/index.html)
* [Datastax. Docs](https://docs.datastax.com/en/developer/java-driver/4.17/manual/core/index.html)
* [Datastax. Cassandra/Scylla examples](https://github.com/datastax/cassandra-reactive-demo/tree/master)
* [Scylladb. Sources](https://github.com/scylladb/scylladb)
* [Scylladb. Functions](https://opensource.docs.scylladb.com/stable/cql/functions.html)
* [Scylladb. Video-streaming-app](https://www.scylladb.com/2024/01/09/build-a-low-latency-video-streaming-app/)
* [Scylladb. Aggregates](https://www.scylladb.com/2023/06/20/how-scylladb-distributed-aggregates-reduce-query-execution-time-up-to-20x/)
* https://www.baeldung.com/spring-boot-actuators
* https://github.com/hendisantika/spring-boot-prometheus-grafana/blob/master/README.md
* https://habr.com/ru/articles/548700/
* https://github.com/ablx/monitoring_stack
* https://prometheus.io/docs/prometheus/latest/querying/functions/#rate
* https://opentelemetry.io/docs/languages/java/intro/
