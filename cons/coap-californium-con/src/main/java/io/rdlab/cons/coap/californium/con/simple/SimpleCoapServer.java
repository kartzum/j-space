package io.rdlab.cons.coap.californium.con.simple;

import com.sun.management.UnixOperatingSystemMXBean;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

public class SimpleCoapServer extends CoapServer {
    static {
        CoapConfig.register();
        UdpConfig.register();
        TcpConfig.register();
    }

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCoapServer.class);

    public static SimpleCoapServer create(
            String host,
            int port,
            Configuration configuration,
            List<CoapResource> coapResources
    ) {
        UnixOperatingSystemMXBean osMBean =
                (UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
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

        CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
        builder.setInetSocketAddress(inetSocketAddress);

        builder.setConfiguration(configuration);

        SimpleCoapServer simpleCoapServer = new SimpleCoapServer();
        coapResources.forEach(simpleCoapServer::add);

        simpleCoapServer.addEndpoint(builder.build());

        return simpleCoapServer;
    }
}
