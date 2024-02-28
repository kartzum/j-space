package io.rdlab.j17t.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Operations {
    public static void main(String[] args) {
        GroupingBy.run();
    }

    private static class GroupingBy {
        static void run() {
            Map<String, List<Worker>> positions = createWorkers()
                    .collect(Collectors.groupingBy(Worker::getPosition));
            positions.forEach((k, v) -> System.out.println(k + ", " + v.stream().toList()));
            System.out.println();
        }

        private static Stream<Worker> createWorkers() {
            return Stream.of(
                    new Worker("WorkerA", 23, 151, "R"),
                    new Worker("WorkerB", 33, 152, "Y"),
                    new Worker("WorkerC", 31, 153, "Y")
            );
        }
    }
}
