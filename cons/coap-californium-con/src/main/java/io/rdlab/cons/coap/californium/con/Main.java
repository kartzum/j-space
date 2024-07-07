package io.rdlab.cons.coap.californium.con;

import com.sun.management.UnixOperatingSystemMXBean;
import io.rdlab.cons.coap.californium.con.simple.ServerTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

import static io.rdlab.cons.coap.californium.con.util.Utils.getEnv;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length > 1) {
            String name = args[0];
            String value = args[1];
            if (!"-e".equals(name)) {
                return;
            }
            if ("scs".equals(value)) {
                ServerTerminal.run(args);
            } else if ("s".equals(value)) {
                UnixOperatingSystemMXBean osMBean =
                        (UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                LOG.info(
                        "System. open: {}, max: {}.",
                        osMBean.getOpenFileDescriptorCount(),
                        osMBean.getMaxFileDescriptorCount()
                );
            }
        } else {
            String value = getEnv("CONS_N", "scs");
            if ("scs".equals(value)) {
                ServerTerminal.run();
            }
        }
    }
}
