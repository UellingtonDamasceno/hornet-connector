package io.github.ucd.hornet.connector.configs;

import io.github.ucd.hornet.connector.enums.FlowDirection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Uellington Damasceno
 */
public class HornetConfig {

    private static final Logger logger = Logger.getLogger(HornetConfig.class.getName());

    private static final String DLT_PROTOCOL = "http";
    private static final String DLT_URL = "127.0.0.1";
    private static final int DLT_PORT = 3000;

    private final String protocol;
    private final String url;
    private final int port;

    private HornetConfig(String protocol, String url, int port) {
        this.protocol = protocol;
        this.url = url;
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    public static HornetConfig load(FlowDirection direction) {
        logger.log(Level.INFO, "READING {0} HONET CLIENT CONFIG.", direction);

        String prefix = direction.prefix();

        String protocol = System.getenv(prefix + "DLT_PROTOCOL");
        String url = System.getenv(prefix + "DLT_URL");
        String portStr = System.getenv(prefix + "DLT_PORT");

        protocol = (protocol != null && !protocol.isEmpty()) ? protocol : DLT_PROTOCOL;
        url = (url != null && !url.isEmpty()) ? url : DLT_URL;
        Integer port = (portStr != null && !portStr.isEmpty()) ? Integer.valueOf(portStr) : DLT_PORT;

        if (protocol == null || protocol.isEmpty()) {
            logger.log(Level.WARNING, "Using default value for DLT_PROTOCOL: {0}", DLT_PROTOCOL);
        }

        if (url == null || url.isEmpty()) {
            logger.log(Level.WARNING, "Using default value for DLT_URL: {0}", DLT_URL);
        }

        if (port == DLT_PORT) {
            logger.log(Level.WARNING, "Using default value for DLT_PORT: {0}", DLT_PORT);
        }
        
        logger.log(Level.INFO,
                "Loaded configuration for {0}: Protocol = {1}, URL = {2}, Port = {3}",
                new Object[]{direction, protocol, url, port});

        return new HornetConfig(protocol, url, port);
    }

}
