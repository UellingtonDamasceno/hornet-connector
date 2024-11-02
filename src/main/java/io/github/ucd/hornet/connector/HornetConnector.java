package io.github.ucd.hornet.connector;

import io.github.ucd.hornet.connector.configs.CommonConfig;
import io.github.ucd.hornet.connector.enums.FlowDirection;
import io.github.ucd.hornet.connector.model.HornetClient;
import io.github.ucd.hornet.connector.services.BridgeService;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 *
 * @author Uellington Damasceno
 */
public class HornetConnector {

    private static final Logger logger = Logger.getLogger(HornetConnector.class.getName());
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public static void main(String[] args) {

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        logger.info("HonnetConnector - Starting...");
        CommonConfig commonConfig = CommonConfig.load();

        HornetClient upDownClient = HornetClient.updownClient(commonConfig);
        HornetClient downUpClient = HornetClient.downUpClient(commonConfig);

        BridgeService upBridge = new BridgeService(upDownClient, downUpClient, FlowDirection.UP_DOWN);
        BridgeService downBridge = new BridgeService(downUpClient, upDownClient, FlowDirection.DOWN_UP);

        upBridge.start();
        downBridge.start();
        logger.info("HonnetConnector - Configuring greaceful shudown.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            upBridge.stop();
            downBridge.stop();
        }));

        logger.info("HonnetConnector - gracefull shudown configurated.");
        try {
            logger.info("HonnetConnector - Latch waiting shutdown signal.");
            logger.info("HonnetConnector - Started!");
            shutdownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
