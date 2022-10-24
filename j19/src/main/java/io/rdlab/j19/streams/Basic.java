package io.rdlab.j19.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Basic {
    public static void main(String[] args) {
        streamOfCollection();
        streamPipeline();
        theReduceMethod();
        theCollectMethod();
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

    private static void theReduceMethod() {
        System.out.println(Stream.of(1, 2, 3)
                .reduce(10, (a, b) -> a + b, (a, b) -> a + b));
    }

    private static void theCollectMethod() {
        List<Product> productList = Arrays.asList(new Product(23, "potatoes"),
                new Product(14, "orange"), new Product(13, "lemon"),
                new Product(23, "bread"), new Product(13, "sugar"));

        Map<Double, List<Product>> collectorMapOfLists = productList.stream()
                .collect(Collectors.groupingBy(Product::getPrice));

        collectorMapOfLists.forEach((i, j) -> System.out.println(i.toString() + j));
    }

    private static class Product {
        private String name;
        private double price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public Product(double price, String name) {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "name='" + name + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
}
