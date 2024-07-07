package io.rdlab.cons.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TinyClient implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(TinyClient.class);

    private DatagramSocket socket;
    private InetAddress address;
    private final String host;
    private final int port;
    private final boolean logging;

    public TinyClient(
            String host,
            int port,
            boolean logging
    ) {
        this.host = host;
        this.port = port;
        this.logging = logging;
    }

    public static TinyClient create(
            String host,
            int port,
            boolean logging
    ) {
        return new TinyClient(host, port, logging);
    }

    public Response exchange(byte[] data) {
        if (data == null) {
            return null;
        }
        if (address == null) {
            try {
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        if (socket == null) {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                if (logging) {
                    LOG.error(e.getMessage(), e);
                }
                throw new RuntimeException(e);
            }
            if (logging) {
                LOG.info("Prepared. host: {}, port: {}.", host, port);
            }
        }
        DatagramPacket requestPacket = new DatagramPacket(data, data.length, address, port);
        try {
            socket.send(requestPacket);
        } catch (IOException e) {
            if (logging) {
                LOG.error(e.getMessage(), e);
            }
            throw new RuntimeException(e);
        }
        byte[] responseBuffer = new byte[data.length];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        try {
            socket.receive(responsePacket);
        } catch (IOException e) {
            if (logging) {
                LOG.error(e.getMessage(), e);
            }
            throw new RuntimeException(e);
        }
        return new Response(responsePacket.getData(), responsePacket.getLength());
    }

    @Override
    public void close() {
        if (socket != null) {
            if (logging) {
                LOG.info("Stop. host: {}, port: {}.", host, port);
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
            socket = null;
            address = null;
        }
    }

    public record Response(byte[] data, int length) {
    }
}
