package io.rdlab.java.concurrency.wait.notify;

public final class Start {
    private Start() {
    }

    static class CoffeeMachine extends Thread {
        static final Object lock = new Object();

        static String coffeeMade = null;
        static int coffeeNumber = 1;

        void makeCoffee() {
            synchronized (CoffeeMachine.lock) {
                if (coffeeMade != null) {
                    try {
                        System.out.println("Coffee machine: Waiting for waiter notification to deliver the coffee");
                        CoffeeMachine.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                coffeeMade = "Coffee No. " + coffeeNumber++;
                System.out.println("Coffee machine says: Made " + coffeeMade);
                CoffeeMachine.lock.notifyAll();
                System.out.println("Coffee machine: Notifying waiter to pick the coffee");
            }
        }

        @Override
        public void run() {
            while (true) {
                makeCoffee();
                try {
                    System.out.println("Coffee machine: Making another coffee now");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class Waiter extends Thread {
        void getCoffee() {
            synchronized (CoffeeMachine.lock) {
                if (CoffeeMachine.coffeeMade == null) {
                    System.out.println("Waiter: Will get orders till coffee machine notifies me ");

                    try {
                        CoffeeMachine.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Waiter: Delivering " + CoffeeMachine.coffeeMade);
                CoffeeMachine.coffeeMade = null;
                CoffeeMachine.lock.notifyAll();
                System.out.println("Waiter: Notifying coffee machine to make another one");
            }
        }

        @Override
        public void run() {
            while (true) {
                getCoffee();
            }
        }
    }

    public static void start() {
        final CoffeeMachine coffeeMachine = new CoffeeMachine();
        final Waiter waiter = new Waiter();
        coffeeMachine.start();
        waiter.start();
    }
}

