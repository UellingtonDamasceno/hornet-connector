package io.github.ucd.hornet.connector.services;

import dlt.client.tangle.hornet.model.transactions.Transaction;
import dlt.client.tangle.hornet.services.ILedgerSubscriber;
import io.github.ucd.hornet.connector.model.HornetClient;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Uellington Damasceno
 */
public class BridgeService implements ILedgerSubscriber {

    private HornetClient reader;
    private HornetClient writer;

    public BridgeService(HornetClient upDownClient, HornetClient donwUpClient) {
        this.reader = upDownClient;
        this.writer = donwUpClient;
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
        if (!transaction.isMultiLayerTransaction()) {
            return;
        }
        try {
            this.writer.sendTransaction(transaction);
        } catch (InterruptedException ex) {
            Logger.getLogger(BridgeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
