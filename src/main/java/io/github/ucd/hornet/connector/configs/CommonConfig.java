package io.github.ucd.hornet.connector.configs;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Uellington Damasceno
 */
public class CommonConfig {

    private static final int BUFFER_MAX_LEN = 128;
    private static final boolean DEBUG_MODE_VALUE = false;

    private final int bufferMaxLen;
    private final boolean debugModeValue;

    private static final Logger logger = Logger.getLogger(CommonConfig.class.getName());

    private CommonConfig(int bufferMaxLen, boolean debugModeValue) {
        this.bufferMaxLen = bufferMaxLen;
        this.debugModeValue = debugModeValue;
    }

    public int getBufferMaxLen() {
        return bufferMaxLen;
    }

    public boolean isDebugModeValue() {
        return debugModeValue;
    }

    public static CommonConfig load() {
        String bufferMaxLenStr = System.getenv("BUFFER_MAX_LEN");
        String debugModeValueStr = System.getenv("DEBUG_MODE_VALUE");

        int bufferMaxLen = (bufferMaxLenStr != null && !bufferMaxLenStr.isEmpty())
                ? Integer.parseInt(bufferMaxLenStr) : BUFFER_MAX_LEN;

        boolean debugModeValue = (debugModeValueStr != null && !debugModeValueStr.isEmpty())
                ? Boolean.parseBoolean(debugModeValueStr) : DEBUG_MODE_VALUE;

        logger.log(Level.INFO,
                "Loaded configuration: BufferMaxLen = {0}, DebugModeValue = {1}",
                new Object[]{bufferMaxLen, debugModeValue});

        if (bufferMaxLenStr == null || bufferMaxLenStr.isEmpty()) {
            logger.log(Level.WARNING, "Using default value for BufferMaxLen: {0}", BUFFER_MAX_LEN);
        }

        if (debugModeValueStr == null || debugModeValueStr.isEmpty()) {
            logger.log(Level.WARNING, "Using default value for DebugModeValue: {0}", DEBUG_MODE_VALUE);
        }

        return new CommonConfig(bufferMaxLen, debugModeValue);
    }
}
