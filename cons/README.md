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

Run client.
```
java -XX:+UseG1GC -Xmx6144m -cp ./cf-extplugtest-client-3.12.0-SNAPSHOT.jar "org.eclipse.californium.extplugtests.BenchmarkClient" coap://localhost:7012/benchmark?rlen=1 --clients=600 --requests=100000
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
