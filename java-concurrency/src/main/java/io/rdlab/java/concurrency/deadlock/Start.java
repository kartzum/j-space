package io.rdlab.java.concurrency.deadlock;

public final class Start {
    private Start() {
    }

    static class Balls {
        static long balls = 0;
    }

    static class Runs {
        static long runs = 0;
    }

    static class Counter implements Runnable {
        @Override
        public void run() {
            incBallAfterRun();
            incRunAfterBall();
        }

        void incBallAfterRun() {
            synchronized (Runs.class) {
                synchronized (Balls.class) {
                    Runs.runs++;
                    Balls.balls++;
                }
            }
        }

        void incRunAfterBall() {
            synchronized (Balls.class) {
                synchronized (Runs.class) {
                    Balls.balls++;
                    Runs.runs++;
                }
            }
        }
    }

    public static void start() {
        final Counter counter = new Counter();
        final Thread thread1 = new Thread(counter);
        final Thread thread2 = new Thread(counter);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
