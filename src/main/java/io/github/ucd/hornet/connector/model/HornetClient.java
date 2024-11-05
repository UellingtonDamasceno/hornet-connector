package io.github.ucd.hornet.connector.model;

import dlt.client.tangle.hornet.model.LedgerReader;
import dlt.client.tangle.hornet.model.LedgerWriter;
import dlt.client.tangle.hornet.model.ZMQServer;
import dlt.client.tangle.hornet.model.transactions.IndexTransaction;
import dlt.client.tangle.hornet.model.transactions.Transaction;
import dlt.client.tangle.hornet.services.ILedgerSubscriber;
import io.github.ucd.hornet.connector.configs.CommonConfig;
import io.github.ucd.hornet.connector.configs.HornetConfig;
import io.github.ucd.hornet.connector.configs.ZMQConfig;
import io.github.ucd.hornet.connector.enums.FlowDirection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Uellington Damasceno
 */
public class HornetClient {

    private static final Logger logger = Logger.getLogger(HornetClient.class.getName());

    private final LedgerReader reader;
    private final LedgerWriter writer;
    private final ZMQServer server;
    private final FlowDirection flowDirection;

    private HornetClient(LedgerReader reader, LedgerWriter writer, FlowDirection direction, ZMQServer server) {
        this.reader = reader;
        this.writer = writer;
        this.server = server;
        this.flowDirection = direction;
    }

    public boolean isFlowDirection(FlowDirection flowDirection) {
        return this.flowDirection == flowDirection;
    }

    public void start() {
        logger.log(Level.INFO, "ZMQ server is starting - {0}", flowDirection.name());
        this.server.start();
        logger.log(Level.INFO, "Reader is starting - {0}", flowDirection.name());
        this.reader.start();
        logger.log(Level.INFO, "Writer is starting - {0}", flowDirection);
        this.writer.start();
    }

    public void stop() {
        logger.log(Level.INFO, "Writer is stoping - {0}", flowDirection);
        this.writer.stop();
        logger.log(Level.INFO, "Reader is stoping - {0}", flowDirection);
        this.reader.stop();
        logger.log(Level.INFO, "ZMQ server is stopping - {0}", flowDirection.name());
        this.server.stop();
    }

    public void subscribe(String topic, ILedgerSubscriber subscriber) {
        logger.log(Level.INFO,
                "Has a new subscribe at HonetClient {0} in the topic: {1}",
                new Object[]{flowDirection, topic});
        this.reader.subscribe(topic, subscriber);
    }

    public void sendTransaction(Transaction transaction) throws InterruptedException {
        IndexTransaction indexedTransaction = new IndexTransaction(transaction.getType().name(), transaction);
        this.writer.put(indexedTransaction);
    }

    public static HornetClient updownClient(CommonConfig commonConfig) {
        logger.info("Creating UP_DOWN HonetClient.");
        FlowDirection flowDirection = FlowDirection.UP_DOWN;
       return HornetClient.createClient(commonConfig, flowDirection);
    }

    public static HornetClient downUpClient(CommonConfig commonConfig) {
        logger.info("Creating DOWN_UP HonetClient.");
        FlowDirection flowDirection = FlowDirection.DOWN_UP;
        return HornetClient.createClient(commonConfig, flowDirection);
    }
    
    private static HornetClient createClient(CommonConfig commonConfig, FlowDirection flowDirection){
        HornetConfig honetConfig = HornetConfig.load(flowDirection);
        ZMQConfig zmqConfig = ZMQConfig.load(flowDirection);

        ZMQServer server = new ZMQServer(commonConfig.getBufferMaxLen(),
                zmqConfig.getProtocol(),
                zmqConfig.getUrl(),
                zmqConfig.getPort());

        String honetProtocol = honetConfig.getProtocol();
        String honetUrl = honetConfig.getUrl();
        int honetPort = honetConfig.getPort();

        LedgerReader reader = new LedgerReader(honetProtocol,
                honetUrl,
                honetPort);
        
        reader.setServer(server);
        reader.setDebugModeValue(commonConfig.isDebugModeValue());

        LedgerWriter writer = new LedgerWriter(honetProtocol,
                honetUrl,
                honetPort,
                commonConfig.getBufferMaxLen());

        writer.setDebugModeValue(commonConfig.isDebugModeValue());

        return new HornetClient(reader, writer, flowDirection, server);
    }

}
