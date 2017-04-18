package io.rdlab.java.concurrency.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public final class Start {
    private Start() {
    }

    static class Kernel extends UntypedActor {
        public Kernel() {
        }

        @Override
        public void onReceive(Object arg0) throws Exception {
            if (arg0 instanceof String) {
                String s = (String) arg0;
                System.out.println(s);
                return;
            }
            unhandled(arg0);
        }
    }

    public static void start() {
        InputStream source;
        try {
            source = new ByteArrayInputStream("A B C exit".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        final ActorSystem system = ActorSystem.create("learning2hard");

        final ActorRef kernel = system.actorOf(Props.create(Kernel.class), "kernel");
        final Scanner sc = new Scanner(source);
        while (sc.hasNext()) {
            final String f = sc.nextLine();
            if (f.equals("exit")) break;
            kernel.tell(f, ActorRef.noSender());
        }

        system.shutdown();
    }
}
