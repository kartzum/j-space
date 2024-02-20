# j-space
Java Examples

## Tasks & Questions

* [java-interview](https://github.com/enhorse/java-interview)

## Projects

### g-start
Simple java + gradle start application.

### java-concurrency
Examples of concurrency in Java.

#### Create Thread
io.rdlab.java.concurrency.simple.Start
```
final Thread thread = new Thread(() -> System.out.println(Thread.currentThread().getName()));
thread.start();
```

#### Deadlocks
io.rdlab.java.concurrency.deadlock.Start
```
A deadlock arises when locking threads result in a situation where they cannot proceed and thus wait indefinitely
for others to terminate.
```

#### Livelocks
```
To help understand livelocks, let’s consider an analogy. Assume that there are two robotic cars that are programmed
to automatically drive in the road. There is a situation where two robotic cars reach the two opposite ends of a narrow
bridge. The bridge is so narrow that only one car can pass through at a time. The robotic cars are programmed such
that they wait for the other car to pass through first. When both the cars attempt to enter the bridge at the same time,
the following situation could happen: each car starts to enter the bridge, notices that the other car is attempting to do
the same, and reverses! Note that the cars keep moving forward and backward and thus appear as if they’re doing lots
of work, but there is no progress made by either of the cars. This situation is called a livelock.
```

#### Lock Starvation
```
Consider the situation in which numerous threads have different priorities assigned to them (in the range of lowest
priority, 1, to highest priority, 10, which is the range allowed for priority of threads in Java). When a mutex lock is
available, the thread scheduler will give priority to the threads with high priority over low priority. If there are many
high-priority threads that want to obtain the lock and also hold the lock for long time periods, when will the low-priority
threads get a chance to obtain the lock? In other words, in a situation where low-priority threads “starve” for a long
time trying to obtain the lock is known as lock starvation.
```

#### The Wait/Notify Mechanism
io.rdlab.java.concurrency.wait.notify.Start

#### Fork/Join Framework
io.rdlab.java.concurrency.fork.join.Start

#### akka
io.rdlab.java.concurrency.akka.Start

#### Links
* [Java Concurrency / Multithreading Tutorial](http://tutorials.jenkov.com/java-concurrency/index.html)
* [Fork/Join Framework](https://habrahabr.ru/post/128985/)
* [JDK concurrent package](https://habrahabr.ru/post/187854/)
* [Реактивные акторы на java](https://habrahabr.ru/post/232897/)
* [Some example code of using Akka from Java](https://github.com/fhopf/akka-crawler-example)
* [Modern Java - A Guide to Java 8 http://winterbe.com](https://github.com/winterbe/java8-tutorial)

### j19
j19.

#### loom

* [structured-concurrency](https://stackoverflow.com/questions/73229247/how-can-i-run-jdk-19-with-structured-concurrency)

#### streams
* [The Java 8 Stream API Tutorial](https://www.baeldung.com/java-8-streams)

#### postgresql
* [PostgreSQL Java](https://zetcode.com/java/postgresql/)

### sbj
sbj.

### sbk
sbk.

### db

#### psql_1

```
docker-compose up
```

* [Запускаем PostgreSQL в Docker: от простого к сложному](https://habr.com/ru/post/578744/)
* [Docker Compose Postgres initdb](https://onexlab-io.medium.com/docker-compose-postgres-initdb-ba0021deef76)

#### psql_hn

### hn

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Spring Boot PostgreSQL](https://zetcode.com/springboot/postgresql/)
* [Spring Boot 2 + JUnit 5 + Mockito](https://frontbackend.com/spring-boot/spring-boot-2-junit-5-mockito)

### j20
j20.

* [JAVA 20 FEATURES](https://www.happycoders.eu/java/java-20-features/)

### jlc17
Project template for Java 17.

### j17t
Project with tasks.

## design

### microservices
* [API Gateway](https://habr.com/ru/companies/rosbank/articles/779770/)
* [SAGA](https://learn.microsoft.com/ru-ru/azure/architecture/reference-architectures/saga/saga)

### networks
* https://selectel.ru/blog/osi-for-beginners/
* https://habr.com/ru/articles/354408/
* https://selectel.ru/blog/l3vpn-l2vpn/
* https://habr.com/ru/articles/215117/
* https://habr.com/ru/articles/258285/
* [В чем разница между RPC и REST?](https://aws.amazon.com/ru/compare/the-difference-between-rpc-and-rest/)
* [gRPC в качестве протокола межсервисного взаимодействия. Доклад Яндекса](https://habr.com/ru/companies/yandex/articles/484068/)
* [Eventual Consistency vs. Strong Eventual Consistency vs. Strong Consistency](https://www.baeldung.com/cs/eventual-consistency-vs-strong-eventual-consistency-vs-strong-consistency)
