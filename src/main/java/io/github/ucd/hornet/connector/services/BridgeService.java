package io.github.ucd.hornet.connector.services;

import com.google.common.cache.Cache;
import dlt.client.tangle.hornet.model.transactions.Transaction;
import dlt.client.tangle.hornet.services.ILedgerSubscriber;
import io.github.ucd.hornet.connector.enums.FlowDirection;
import io.github.ucd.hornet.connector.model.HornetClient;
import java.util.List;
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

    private final Cache<Integer, Boolean> sharedRecentlyForwardedMessageIds;

    public BridgeService(HornetClient upDownClient, HornetClient donwUpClient, FlowDirection direction, Cache forwarededMessageCache) {
        this.reader = upDownClient;
        this.writer = donwUpClient;
        this.direction = direction;
        this.sharedRecentlyForwardedMessageIds = forwarededMessageCache;
    }

    public void start() {
        this.defaultTopicsList().forEach(t -> this.reader.subscribe(t, this));
        this.reader.start();
        this.writer.start();
    }

    public void stop() {
        this.writer.stop();
        this.reader.stop();
    }

    @Override
    public void update(Object trans, Object transactionId) {
        if (trans == null) {
            return;
        }
        
        Transaction transaction = (Transaction) trans;
        
        if (!transaction.isMultiLayerTransaction()) {
            return;
        }

        logger.log(Level.INFO, "{0} - {1} ", new Object[]{direction, transaction});
        int transHash = transaction.hashCode();
        
        if (this.sharedRecentlyForwardedMessageIds.getIfPresent(transHash) != null) {
            logger.log(Level.WARNING, "ECHO - {0} - {1}", new Object[]{direction, transaction});
            return;
        }
        
        try {
            this.writer.sendTransaction(transaction);
            this.sharedRecentlyForwardedMessageIds.put(transHash, true);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private List<String> defaultTopicsList() {
        return List.of(
                "LB_MULTI_REQUEST",
                "LB_MULTI_RESPONSE",
                "LB_MULTI_DEVICE_REQUEST",
                "LB_MULTI_DEVICE_RESPONSE"
        );
    }
}
