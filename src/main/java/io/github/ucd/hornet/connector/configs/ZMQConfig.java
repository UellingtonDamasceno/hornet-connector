package io.github.ucd.hornet.connector.configs;

import io.github.ucd.hornet.connector.enums.FlowDirection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Uellington Damasceno
 */
public class ZMQConfig {

    private static final Logger logger = Logger.getLogger(ZMQConfig.class.getName());

    private static final String ZMQ_PROTOCOL = "tcp";
    private static final String ZMQ_URL = "127.0.0.1";
    private static final String ZMQ_PORT = "5556";

    private final String protocol;
    private final String url;
    private final String port;

    private ZMQConfig(String protocol, String url, String port) {
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

    public String getPort() {
        return port;
    }

    public static ZMQConfig load(FlowDirection direction) {
        String prefix = direction.prefix();

        String protocol = System.getenv(prefix + "ZMQ_PROTOCOL");
        String url = System.getenv(prefix + "ZMQ_URL");
        String portStr = System.getenv(prefix + "ZMQ_PORT");

        protocol = (protocol != null && !protocol.isEmpty()) ? protocol : ZMQ_PROTOCOL;
        url = (url != null && !url.isEmpty()) ? url : ZMQ_URL;
        portStr = (portStr != null && !portStr.isEmpty()) ? portStr : ZMQ_PORT;

        logger.log(Level.INFO,
                "Loaded configuration for {0}: Protocol = {1}, URL = {2}, Port = {3}",
                new Object[]{direction, protocol, url, portStr});

        if (protocol == null || protocol.isEmpty()) {
            logger.warning("Using default value for Protocol: " + ZMQ_PROTOCOL);
        }

        if (url == null || url.isEmpty()) {
            logger.warning("Using default value for URL: " + ZMQ_URL);
        }

        if (portStr == null || portStr.isEmpty()) {
            logger.warning("Using default value for Port: " + ZMQ_PORT);
        }

        return new ZMQConfig(protocol, url, portStr);
    }
}
