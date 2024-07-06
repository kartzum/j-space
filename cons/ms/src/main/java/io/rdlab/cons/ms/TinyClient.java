package io.rdlab.cons.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TinyClient {
    private static final Logger LOG = LoggerFactory.getLogger(TinyClient.class);

    private DatagramSocket socket;
    private InetAddress address;
    private final String host;
    private final int port;

    public TinyClient(
            String host,
            int port
    ) {
        this.host = host;
        this.port = port;
    }

    public static TinyClient create(String host, int port) {
        return new TinyClient(host, port);
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
                throw new RuntimeException(e);
            }
            LOG.info("Prepared. port: {}.", port);
        }
        DatagramPacket requestPacket = new DatagramPacket(data, data.length, address, port);
        try {
            socket.send(requestPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] responseBuffer = new byte[data.length];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        try {
            socket.receive(responsePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Response(responsePacket.getData(), responsePacket.getLength());
    }

    public void stop() {
        if (socket != null) {
            LOG.info("Stop. port: {}.", port);
            socket.close();
            socket = null;
            address = null;
        }
    }

    public record Response(byte[] data, int length) {
    }
}
