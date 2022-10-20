package io.rdlab.j19.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Basic {
    public static void main(String[] args) {
        streamOfCollection();
        streamPipeline();
    }

    private static void streamOfCollection() {
        Collection<String> collection = Arrays.asList("a", "b", "c");
        Stream<String> streamOfCollection = collection.stream();
        System.out.println(streamOfCollection.toList());
    }

    private static void streamPipeline() {
        List<String> list = Arrays.asList("ab11", "ab22", "ab33");
        System.out.println(
                list.stream().skip(1).map(element -> element.substring(0, 3)).sorted().toList()
        );
    }
}
