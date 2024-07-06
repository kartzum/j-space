package io.rdlab.cons.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TinyServer {
    private static final Logger LOG = LoggerFactory.getLogger(TinyServer.class);

    private final String host;
    private final int port;
    private final int bufferSize;

    private final ExecutorService packetProcessingExecutorService;

    private final Handler handler;

    private final ExecutorService receiverProcessingExecutorService;

    private DatagramSocket serverSocket;
    private boolean running;

    public TinyServer(
            String host,
            int port,
            int bufferSize,
            Handler handler
    ) {
        this.host = host;
        this.port = port;
        this.bufferSize = bufferSize;
        this.packetProcessingExecutorService = Executors.newFixedThreadPool(1);
        this.handler = handler;
        this.receiverProcessingExecutorService = Executors.newSingleThreadExecutor();
    }

    public static TinyServer create(
            String host,
            int port,
            int bufferSize,
            Handler handler
    ) {
        return new TinyServer(host, port, bufferSize, handler);
    }

    public void run() {
        LOG.info("Starting. host: {}, port: {}.", host, port);
        InetAddress addr;
        try {
            addr = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(addr, port);
        try {
            serverSocket = new DatagramSocket(inetSocketAddress);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        running = true;
        receiverProcessingExecutorService.submit(new Receiver());
    }

    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        LOG.info("Stopping. host: {}, port: {}.", host, port);
        if (serverSocket != null) {
            serverSocket.close();
        }
        LOG.info("Stop. host: {}, port: {}.", host, port);
    }

    private class Receiver implements Runnable {
        @Override
        public void run() {
            byte[] buffer = new byte[bufferSize];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            LOG.info("Start. host: {}, port: {}.", host, port);
            while (running) {
                if (!running) {
                    break;
                }
                try {
                    serverSocket.receive(receivePacket);
                } catch (IOException e) {
                }
                packetProcessingExecutorService.submit(new PacketProcessor(serverSocket, receivePacket, handler));
            }
        }
    }

    private static class PacketProcessor implements Runnable {
        private final DatagramSocket serverSocket;
        private final DatagramPacket receivePacket;
        private final Handler handler;

        public PacketProcessor(
                DatagramSocket serverSocket,
                DatagramPacket receivePacket,
                Handler handler
        ) {
            this.serverSocket = serverSocket;
            this.receivePacket = receivePacket;
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                byte[] receiveBytes = Arrays.copyOfRange(
                        receivePacket.getData(),
                        receivePacket.getOffset(),
                        receivePacket.getLength()
                );
                byte[] sendData = handler.handle(receiveBytes);
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData,
                        sendData.length,
                        receivePacket.getAddress(),
                        receivePacket.getPort()
                );
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface Handler {
        byte[] handle(byte[] data);
    }
}
