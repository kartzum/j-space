package io.rdlab.cons.ms;

import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TinyServer implements Runnable, Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(TinyServer.class);

    private final String host;
    private final int port;
    private final int nThreads;
    private final int bufferSize;

    private final ExecutorService packetProcessingExecutorService;

    private final Handler handler;

    private final ExecutorService receiverProcessingExecutorService;

    private DatagramSocket serverSocket;
    private boolean running;

    public TinyServer(
            String host,
            int port,
            int nThreads,
            int bufferSize,
            Handler handler
    ) {
        this.host = host;
        this.port = port;
        this.nThreads = nThreads;
        this.bufferSize = bufferSize;
        this.packetProcessingExecutorService = Executors.newFixedThreadPool(nThreads);
        this.handler = handler;
        this.receiverProcessingExecutorService = Executors.newSingleThreadExecutor();
    }

    public static TinyServer create(
            String host,
            int port,
            int nThreads,
            int bufferSize,
            Handler handler
    ) {
        return new TinyServer(host, port, nThreads, bufferSize, handler);
    }

    @Override
    public void run() {
        if (running) {
            return;
        }
        UnixOperatingSystemMXBean osMBean =
                (UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        LOG.info("Starting. host: {}, port: {}, nThreads: {}.", host, port, nThreads);
        LOG.info(
                "System. open: {}, max: {}.",
                osMBean.getOpenFileDescriptorCount(),
                osMBean.getMaxFileDescriptorCount()
        );
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
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        running = true;
        receiverProcessingExecutorService.submit(new Receiver());
    }

    @Override
    public void close() {
        if (!running) {
            return;
        }
        running = false;
        LOG.info("Stopping. host: {}, port: {}, nThreads: {}.", host, port, nThreads);
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
        LOG.info("Stop. host: {}, port: {}, nThreads: {}.", host, port, nThreads);
    }

    private class Receiver implements Runnable {
        @Override
        public void run() {
            byte[] buffer = new byte[bufferSize];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            LOG.info("Start. host: {}, port: {}, nThreads: {}.", host, port, nThreads);
            while (running) {
                if (!running) {
                    break;
                }
                try {
                    serverSocket.receive(receivePacket);
                } catch (IOException e) {
                    if (!"Socket closed".equals(e.getMessage())) {
                        LOG.error(e.getMessage(), e);
                    }
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
                if (!"Socket closed".equals(e.getMessage())) {
                    LOG.error(e.getMessage(), e);
                }
                throw new RuntimeException(e);
            }
        }
    }

    public interface Handler {
        byte[] handle(byte[] data);
    }
}
