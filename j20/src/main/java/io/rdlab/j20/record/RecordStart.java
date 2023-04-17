package io.rdlab.j20.record;

public class RecordStart {
    public record Position(int x, int y) {
    }

    public static void main(String[] args) {
        testRecord(new Position(10, 20));
    }

    public static void testRecord(Object obj) {
        switch (obj) {
            case Position(int x, int y) -> System.out.println(x + " " + y);
            default -> System.out.println("Unexpected value: " + obj);
        }
    }
}
