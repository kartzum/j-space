# j-space
Basic Java Examples

## g-start
Simple java + gradle start application.

## java-concurrency
Examples of concurrency in Java.
## Create Thread
io.rdlab.java.concurrency.simple.Start
```
final Thread thread = new Thread(() -> System.out.println(Thread.currentThread().getName()));
thread.start();
```
## Deadlocks
io.rdlab.java.concurrency.deadlock.Start
```
A deadlock arises when locking threads result in a situation where they cannot proceed and thus wait indefinitely
for others to terminate.
```
## Livelocks
```
To help understand livelocks, let’s consider an analogy. Assume that there are two robotic cars that are programmed
to automatically drive in the road. There is a situation where two robotic cars reach the two opposite ends of a narrow
bridge. The bridge is so narrow that only one car can pass through at a time. The robotic cars are programmed such
that they wait for the other car to pass through first. When both the cars attempt to enter the bridge at the same time,
the following situation could happen: each car starts to enter the bridge, notices that the other car is attempting to do
the same, and reverses! Note that the cars keep moving forward and backward and thus appear as if they’re doing lots
of work, but there is no progress made by either of the cars. This situation is called a livelock.
```
## Lock Starvation
```
Consider the situation in which numerous threads have different priorities assigned to them (in the range of lowest
priority, 1, to highest priority, 10, which is the range allowed for priority of threads in Java). When a mutex lock is
available, the thread scheduler will give priority to the threads with high priority over low priority. If there are many
high-priority threads that want to obtain the lock and also hold the lock for long time periods, when will the low-priority
threads get a chance to obtain the lock? In other words, in a situation where low-priority threads “starve” for a long
time trying to obtain the lock is known as lock starvation.
```
## Links
* [Java Concurrency / Multithreading Tutorial](http://tutorials.jenkov.com/java-concurrency/index.html)