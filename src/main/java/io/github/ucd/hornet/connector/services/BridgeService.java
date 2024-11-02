package io.github.ucd.hornet.connector.services;

import dlt.client.tangle.hornet.model.transactions.Transaction;
import dlt.client.tangle.hornet.services.ILedgerSubscriber;
import io.github.ucd.hornet.connector.enums.FlowDirection;
import io.github.ucd.hornet.connector.model.HornetClient;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Uellington Damasceno
 */
public class BridgeService implements ILedgerSubscriber {

    private static final Logger logger = Logger.getLogger(HornetClient.class.getName());
    
    private final HornetClient reader;
    private final HornetClient writer;
    private final FlowDirection direction;
    
    public BridgeService(HornetClient upDownClient, HornetClient donwUpClient, FlowDirection direction) {
        this.reader = upDownClient;
        this.writer = donwUpClient;
        this.direction = direction;
    }

    public void start() {
        this.reader.subscribe("LB_*", this);
        this.reader.start();
        this.writer.start();
    }

    public void stop() {
        this.writer.stop();
        this.reader.stop();
    }

    @Override
    public void update(Object trans, Object topic) {
        if (trans == null) {
            return;
        }
        Transaction transaction = (Transaction) trans;
        logger.log(Level.INFO, "{0} - {1} - {2} - {3}", new Object[]{direction, topic, transaction.isMultiLayerTransaction(), transaction});
        if (!transaction.isMultiLayerTransaction()) {
            return;
        }
        try {
            this.writer.sendTransaction(transaction);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
