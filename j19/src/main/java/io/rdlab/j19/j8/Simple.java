package io.rdlab.j19.j8;

import java.util.Collections;
import java.util.function.Function;

public class Simple {

    private static class A {
        interface Operationable {
            int calculate(int x, int y);
        }

        static void calc(Function<Integer, Integer> f) {
            f.apply(0);
        }

        static void test() {
            Operationable operation = (x, y) -> x + y;
            int result = operation.calculate(10, 20);
            System.out.println(result); //30

            calc((n) -> n + 1);

            Function<Integer, Integer> f = (a) -> a + 1;
            System.out.println(f.apply(1));

            Collections.sort(Collections.singletonList(2), (a, b) -> a.compareTo(b));
        }
    }

    public static void main(String[] args) {
        A.test();
    }
}
