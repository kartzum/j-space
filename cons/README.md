# cons
Servers.

## coap-californium-con

### History

#### 2024.07.01

##### 1

Run server.
```
java -Xms2048m -XX:ActiveProcessorCount=1 -jar ./coap-californium-con/target/coap-californium-con-1.0-SNAPSHOT-jar-with-dependencies.jar -e scs \ 
-p scs.local.properties
```
Or.
```
java -XX:+UseG1GC -Xms2048m -XX:ActiveProcessorCount=1 -jar ./coap-californium-con-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Run client.
```
java -XX:+UseG1GC -Xms2048m -XX:ActiveProcessorCount=1 -cp ./cf-extplugtest-client-3.12.0-SNAPSHOT.jar "org.eclipse.californium.extplugtests.BenchmarkClient" coap://localhost:7012/benchmark?rlen=1 --clients=200 --requests=4000
```

```
23:36:41.146 DEBUG [ExecutorsUtil]: remove on cancel: true, split: true, log-diff: 10000
Create 600 benchmark clients, expect to send 60000000 requests overall to coap://localhost:7012/benchmark?rlen=1
23:36:41.157: File: /proc/net/snmp
23:36:41.157: File: /proc/net/snmp6
23:36:41.233: Request:
==[ CoAP Request ]=============================================
MID    : 56250
Token  : A84E69E73A1E3E27
Type   : CON
Method : 0.02 - POST
Options: {"Uri-Host":"localhost", "Uri-Path":"benchmark", "Content-Format":"text/plain", "Uri-Query":"rlen=1", "Accept":"text/plain"}
Payload: 0 Bytes
===============================================================
23:36:41.236: >>> UDP(localhost/127.0.0.1:7012)
23:36:41.272: Received response:
==[ CoAP Response ]============================================
MID    : 56250
Token  : A84E69E73A1E3E27
Type   : ACK
Status : 2.04 - CHANGED
Options: {"Content-Format":"text/plain"}
RTT    : 43 ms
Payload: 1 Bytes
---------------------------------------------------------------
h
===============================================================
Benchmark clients, first request successful.
Benchmark clients created. 142 ms, 4218 clients/s
23:36:41.499: register shutdown hook.
Benchmark started.
[0001]: 205233 requests (20463 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 600 clients)
[0002]: 400572 requests (19534 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 600 clients)
[0003]: 614917 requests (21435 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 600 clients)
[0004]: 718269 requests (10335 reqs/s, 1200 retransmissions (1.16%), 0 transmission errors (0.00%), 600 clients)
[0005]: 777778 requests (5951 reqs/s, 1200 retransmissions (2.01%), 0 transmission errors (0.00%), 600 clients)
[0006]: 786797 requests (902 reqs/s, 1200 retransmissions (13.31%), 0 transmission errors (0.00%), 600 clients)
[0007]: 786797 requests, stale (600 clients, 0 pending)
600 benchmark clients stopped.
 600 clients with 1297 to 1316 requests.
23:37:52.623: uptime: 60110 ms, 8 processors
23:37:52.629: cpu-time: 1480 ms (per-processor: 185 ms, load: 0%)
23:37:52.640: gc: 245 ms, 62 calls
23:37:52.640: average load: 7.28
23:37:52.641: 786197 requests sent, 60000000 expected
23:37:52.641: 786197 requests in 60110 ms, 13079 reqs/s
23:37:52.641: 4275 retransmissions (0.54%)
23:37:52.641: Stale at 786197 messages (1%)
23:37:52.647: connects          : #: 600, avg.: 94.68 ms, 95%: 139 ms, 99%: 144 ms, 99.9%: 155 ms, max.: 155 ms
23:37:52.647: success-responses : #: 785597, avg.: 41.44 ms, 95%: 39 ms, 99%: 54 ms, 99.9%: 3904 ms, max.: 4211 ms
23:37:52.648: errors-responses  : no values available!
23:37:52.648: single-blocks     : #: 785597, avg.: 40.86 ms, 95%: 39 ms, 99%: 54 ms, 99.9%: 3904 ms, max.: 4211 ms
23:37:52.649: coap endpoint statistic:
coap send statistic:
   coap requests                 :   786797 (       0 overall).
   coap responses                :        0 (       0 overall).
   coap acks                     :        0 (       0 overall).
   coap rejects                  :        0 (       0 overall).
   coap request retransmissions  :     4275 (       0 overall).
   coap response retransmissions :        0 (       0 overall).
   coap errors                   :        0 (       0 overall).
coap receive statistic:
   coap requests                 :        0 (       0 overall).
   coap responses                :   786197 (       0 overall).
   coap acks                     :        0 (       0 overall).
   coap rejects                  :        0 (       0 overall).
   coap duplicate requests       :        0 (       0 overall).
   coap duplicate responses      :        0 (       0 overall).
   coap offloaded                :        0 (       0 overall).
   coap ignored                  :      809 (       0 overall).
coap sent 791072, received 787006
```

##### 2
```
java -Xms4096m -jar ./coap-californium-con/target/coap-californium-con-1.0-SNAPSHOT-jar-with-dependencies.jar -e scs \ 
-p scs.local.properties
```

```
java -XX:+UseG1GC -Xmx6144m -cp ./cf-extplugtest-client-3.12.0-SNAPSHOT.jar "org.eclipse.californium.extplugtests.BenchmarkClient" coap://localhost:7012/benchmark?rlen=1 --clients=100 --requests=1000000
```

```
00:01:00.640 DEBUG [ExecutorsUtil]: remove on cancel: true, split: true, log-diff: 10000
Create 100 benchmark clients, expect to send 100000000 requests overall to coap://localhost:7012/benchmark?rlen=1
00:01:00.650: File: /proc/net/snmp
00:01:00.651: File: /proc/net/snmp6
00:01:00.728: Request:
==[ CoAP Request ]=============================================
MID    : 30038
Token  : E0D7762F28948F14
Type   : CON
Method : 0.02 - POST
Options: {"Uri-Host":"localhost", "Uri-Path":"benchmark", "Content-Format":"text/plain", "Uri-Query":"rlen=1", "Accept":"text/plain"}
Payload: 0 Bytes
===============================================================
00:01:00.730: >>> UDP(localhost/127.0.0.1:7012)
00:01:00.735: Received response:
==[ CoAP Response ]============================================
MID    : 30038
Token  : E0D7762F28948F14
Type   : ACK
Status : 2.04 - CHANGED
Options: {"Content-Format":"text/plain"}
RTT    : 10 ms
Payload: 1 Bytes
---------------------------------------------------------------
h
===============================================================
Benchmark clients, first request successful.
Benchmark clients created. 29 ms, 3414 clients/s
00:01:00.784: register shutdown hook.
Benchmark started.
[0001]: 289287 requests (28919 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 100 clients)
[0002]: 544180 requests (25489 reqs/s, 2 retransmissions (0.00%), 0 transmission errors (0.00%), 100 clients)
[0003]: 672603 requests (12842 reqs/s, 57 retransmissions (0.04%), 0 transmission errors (0.00%), 100 clients)
[0004]: 711152 requests (3855 reqs/s, 143 retransmissions (0.37%), 0 transmission errors (0.00%), 100 clients)
[0005]: 720590 requests (944 reqs/s, 197 retransmissions (2.09%), 0 transmission errors (0.00%), 100 clients)
[0006]: 749950 requests (2936 reqs/s, 102 retransmissions (0.35%), 0 transmission errors (0.00%), 100 clients)
[0007]: 753506 requests (356 reqs/s, 194 retransmissions (5.46%), 0 transmission errors (0.00%), 100 clients)
[0008]: 753506 requests, stale (100 clients, 0 pending)
100 benchmark clients stopped.
   2 clients with 4135 to 4182 requests.
  98 clients with 6963 to 7619 requests.
00:02:22.329: uptime: 70144 ms, 8 processors
00:02:22.359: cpu-time: 935 ms (per-processor: 116 ms, load: 0%)
00:02:22.398: gc: 190 ms, 62 calls
00:02:22.399: average load: 10.09
00:02:22.399: 753406 requests sent, 100000000 expected
00:02:22.399: 753406 requests in 70144 ms, 10741 reqs/s
00:02:22.400: 776 retransmissions (0.10%)
00:02:22.400: Stale at 753406 messages (0%)
00:02:22.429: connects          : #: 100, avg.: 7.27 ms, 95%: 13 ms, 99%: 13 ms, 99.9%: 13 ms, max.: 13 ms
00:02:22.430: success-responses : #: 753306, avg.: 7.80 ms, 95%: 9 ms, 99%: 14 ms, 99.9%: 234 ms, max.: 10656 ms
00:02:22.430: errors-responses  : no values available!
00:02:22.431: single-blocks     : #: 753306, avg.: 7.74 ms, 95%: 9 ms, 99%: 14 ms, 99.9%: 234 ms, max.: 10656 ms
00:02:22.434: coap endpoint statistic:
coap send statistic:
   coap requests                 :   753506 (       0 overall).
   coap responses                :        0 (       0 overall).
   coap acks                     :        0 (       0 overall).
   coap rejects                  :        0 (       0 overall).
   coap request retransmissions  :      776 (       0 overall).
   coap response retransmissions :        0 (       0 overall).
   coap errors                   :        0 (       0 overall).
coap receive statistic:
   coap requests                 :        0 (       0 overall).
   coap responses                :   753406 (       0 overall).
   coap acks                     :        0 (       0 overall).
   coap rejects                  :        0 (       0 overall).
   coap duplicate requests       :        0 (       0 overall).
   coap duplicate responses      :        0 (       0 overall).
   coap offloaded                :        0 (       0 overall).
   coap ignored                  :      205 (       0 overall).
coap sent 754282, received 753611
```

#### 2024.07.07

##### 1

```
java -XX:+UseG1GC -Xms2048m -XX:ActiveProcessorCount=1 -cp ./cf-extplugtest-client-3.12.0-SNAPSHOT.jar "org.eclipse.californium.extplugtests.BenchmarkClient" coap://localhost:7012/benchmark?rlen=1 --clients=200 --requests=4000

Create 200 benchmark clients, expect to send 800000 requests overall to coap://localhost:7012/benchmark?rlen=1
21:31:10.475: File: /proc/net/snmp
21:31:10.477: File: /proc/net/snmp6
21:31:10.537: Request:
==[ CoAP Request ]=============================================
MID    : 56091
Token  : BCDE868574C1AE07
Type   : CON
Method : 0.02 - POST
Options: {"Uri-Host":"localhost", "Uri-Path":"benchmark", "Content-Format":"text/plain", "Uri-Query":"rlen=1", "Accept":"text/plain"}
Payload: 0 Bytes
===============================================================
21:31:10.540: >>> UDP(localhost/127.0.0.1:7012)
21:31:10.575: Received response:
==[ CoAP Response ]============================================
MID    : 56091
Token  : BCDE868574C1AE07
Type   : ACK
Status : 2.04 - CHANGED
Options: {"Content-Format":"text/plain"}
RTT    : 41 ms
Payload: 1 Bytes
---------------------------------------------------------------
h
===============================================================
Benchmark clients, first request successful.
Benchmark clients created. 352 ms, 565 clients/s
21:31:10.980: register shutdown hook.
Benchmark started.
[0001]: 66432 requests (6623 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 200 clients)
[0002]: 225063 requests (15863 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 200 clients)
[0003]: 385883 requests (16082 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 200 clients)
[0004]: 537879 requests (15200 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 200 clients)
[0005]: 689971 requests (15209 reqs/s, 0 retransmissions (0.00%), 0 transmission errors (0.00%), 200 clients)
[0006]: 786631 requests (9666 reqs/s, 200 retransmissions (0.21%), 0 transmission errors (0.00%), 200 clients)
200 benchmark clients finished.
   7 clients with 3980 to 3983 requests.
  97 clients with 3984 to 3999 requests.
  81 clients with 4000 to 4014 requests.
  12 clients with 4017 to 4029 requests.
   3 clients with 4032 to 4041 requests.
21:32:19.678: uptime: 67601 ms, 1 processors
21:32:19.680: cpu-time: 978 ms (per-processor: 978 ms, load: 1%)
21:32:19.694: gc: 195 ms, 11 calls
21:32:19.694: average load: 2.09
21:32:19.694: 800000 requests sent, 800000 expected
21:32:19.695: 800000 requests in 67601 ms, 11834 reqs/s
21:32:19.695: 400 retransmissions (0.05%)
21:32:19.699: connects          : #: 200, avg.: 145.45 ms, 95%: 164 ms, 99%: 166 ms, 99.9%: 166 ms, max.: 166 ms
21:32:19.702: success-responses : #: 799800, avg.: 14.63 ms, 95%: 24 ms, 99%: 114 ms, 99.9%: 219 ms, max.: 4990 ms
21:32:19.703: errors-responses  : no values available!
21:32:19.706: single-blocks     : #: 799800, avg.: 10.72 ms, 95%: 19 ms, 99%: 84 ms, 99.9%: 219 ms, max.: 4987 ms
21:32:19.707: coap endpoint statistic:
coap send statistic:
   coap requests                 :   800000 (       0 overall).
   coap responses                :        0 (       0 overall).
   coap acks                     :        0 (       0 overall).
   coap rejects                  :        0 (       0 overall).
   coap request retransmissions  :      400 (       0 overall).
   coap response retransmissions :        0 (       0 overall).
   coap errors                   :        0 (       0 overall).
coap receive statistic:
   coap requests                 :        0 (       0 overall).
   coap responses                :   800000 (       0 overall).
   coap acks                     :        0 (       0 overall).
   coap rejects                  :        0 (       0 overall).
   coap duplicate requests       :        0 (       0 overall).
   coap duplicate responses      :        0 (       0 overall).
   coap offloaded                :        0 (       0 overall).
   coap ignored                  :        0 (       0 overall).
coap sent 800400, received 800000
21:32:19.709: udp4 network statistic:
   udp4 OutDatagrams :  1600400 (       0 overall).
   udp4 InDatagrams  :  1600279 (       0 overall).
   udp4 SndbufErrors :        0 (       0 overall).
   udp4 RcvbufErrors :      121 (       0 overall).
   udp4 InErrors     :      121 (       0 overall).
   udp4 InCsumErrors :        0 (       0 overall).
   udp4 NoPorts      :        0 (       0 overall).
$
```

## ms
Udp-servers.

### Limits.

* https://habr.com/ru/articles/661169/

```
19:39:43.987 [virtual-7296] ERROR io.rdlab.cons.ms.TinyClientTerminal -- java.net.SocketException: Too many open files
java.lang.RuntimeException: java.net.SocketException: Too many open files
	at io.rdlab.cons.ms.TinyClient.exchange(TinyClient.java:61)
	at io.rdlab.cons.ms.RandomTaskGenerator$1.run(RandomTaskGenerator.java:34)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:572)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
	at java.base/java.lang.VirtualThread.run(VirtualThread.java:309)
Caused by: java.net.SocketException: Too many open files
	at java.base/sun.nio.ch.Net.socket0(Native Method)
	at java.base/sun.nio.ch.Net.socket(Net.java:534)
	at java.base/sun.nio.ch.DatagramChannelImpl.<init>(DatagramChannelImpl.java:211)
	at java.base/sun.nio.ch.DatagramChannelImpl.<init>(DatagramChannelImpl.java:183)
	at java.base/sun.nio.ch.SelectorProviderImpl.openUninterruptibleDatagramChannel(SelectorProviderImpl.java:54)
	at java.base/java.net.DatagramSocket.createDelegate(DatagramSocket.java:1413)
	at java.base/java.net.DatagramSocket.<init>(DatagramSocket.java:328)
	at java.base/java.net.DatagramSocket.<init>(DatagramSocket.java:287)
	at io.rdlab.cons.ms.TinyClient.exchange(TinyClient.java:56)
	... 4 common frames omitted
```

Debian.
```
ulimit -n 400000
```

* https://docs.riak.com/riak/kv/2.2.3/using/performance/open-files-limit/

### History

#### 2024.07.07

##### 1

Remote testing.

Run server.
```
java -Xms2048m -XX:ActiveProcessorCount=1 -XX:-MaxFDLimit -jar ./ms-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Run simple load test.
```
java -Xms2048m -XX:ActiveProcessorCount=1 -XX:-MaxFDLimit -jar ./ms-1.0-SNAPSHOT-jar-with-dependencies.jar -e tc -p ./tc.local.m.properties
```

Result.
```
java -Xms2048m -XX:ActiveProcessorCount=1 -XX:-MaxFDLimit -jar ./ms-1.0-SNAPSHOT-jar-with-dependencies.jar -e tc -p ./tc.local.m.properties
19:53:52.733 [main] INFO io.rdlab.cons.ms.TinyClientTerminal -- Start simple load test command. host: localhost, port: 8003, it: 120000, c: 12000.
19:53:52.737 [main] INFO io.rdlab.cons.ms.TinyClientTerminal -- System. open: 5, max: 400000.
19:53:52.772 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 0, ers: 0, avg (~rps): NaN, avg time (ms): NaN, ds: 120000, of: 5, dif: 0.
19:53:53.773 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 11801, ers: 0, avg (~rps): 11801.0, avg time (ms): 0.11, ds: 108199, of: 3731, dif: 11801.
19:53:54.771 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 24160, ers: 0, avg (~rps): 12080.0, avg time (ms): 89.04, ds: 95840, of: 2749, dif: 12359.
19:53:55.777 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 37354, ers: 0, avg (~rps): 12449.66, avg time (ms): 64.09, ds: 82646, of: 4615, dif: 13189.
19:53:56.789 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 48899, ers: 0, avg (~rps): 12224.0, avg time (ms): 91.59, ds: 71101, of: 6586, dif: 11547.
19:53:57.781 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 60739, ers: 0, avg (~rps): 12147.4, avg time (ms): 112.34, ds: 59261, of: 7279, dif: 11841.
19:53:58.785 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 73106, ers: 0, avg (~rps): 12183.66, avg time (ms): 130.11, ds: 46894, of: 8545, dif: 12365.
19:53:59.785 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 84804, ers: 0, avg (~rps): 12114.57, avg time (ms): 135.83, ds: 35196, of: 8937, dif: 11700.
19:54:00.788 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 97071, ers: 0, avg (~rps): 12133.5, avg time (ms): 147.71, ds: 22929, of: 10167, dif: 12266.
19:54:01.789 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 108757, ers: 0, avg (~rps): 12083.77, avg time (ms): 150.51, ds: 11243, of: 10716, dif: 11686.
19:54:02.782 [Timer] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 120000, ers: 0, avg (~rps): 12000.0, avg time (ms): 138.73, ds: 0, of: 10539, dif: 11246.
19:54:03.383 [main] INFO io.rdlab.cons.ms.TinyStatisticsTask -- Rs: 120000, ers: 0, avg (~rps): 12000.0, avg time (ms): 138.73, ds: 0, of: 8, dif: 0.
19:54:03.384 [main] INFO io.rdlab.cons.ms.TinyStatisticsDumpService -- Rs: 120000, ers: 0, avg (~rps): 12000.0, avg time (ms): 138.73, ds: 8, of: 0.
```

