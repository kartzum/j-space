package io.rdlab.java.concurrency.simple;

public final class Start {
    private Start() {
    }

    public static void start() {
        final Thread thread = new Thread(() -> System.out.println(Thread.currentThread().getName()));
        thread.start();
    }
}
