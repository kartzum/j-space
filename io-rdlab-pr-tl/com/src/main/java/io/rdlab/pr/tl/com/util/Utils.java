package io.rdlab.pr.tl.com.util;

import org.awaitility.Awaitility;
import org.awaitility.Duration;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {
    public static InetAddress createInetAddress(String host) {
        try {
            return Inet4Address.getByName(host);
        } catch (UnknownHostException e) {
            try {
                return Inet6Address.getByName(host);
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void await(Duration duration) {
        Awaitility.await()
                .pollDelay(duration)
                .until(() -> true);
    }
}
