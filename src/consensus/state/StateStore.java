package consensus.state;

import common.ConsensusUtils;
import common.CustomLogger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StateStore manages the key-value state for the Paxos-based system.
 * This class provides thread-safe methods to modify and retrieve the state.
 */
public class StateStore {
    private static final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);
    private ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public String get(String key) {
        return store.get(key);
    }

    /**
     * Applies an operation to the state store.
     * @param operation The operation to apply.
     * @return true if the operation is successfully applied, false otherwise.
     */
    public synchronized boolean applyOperation(Operation operation) {
        String key = operation.getKey();
        String value = operation.getValue();

        switch (operation.getOperationType()) {
            case ConsensusUtils.OPERATION_PUT:
                store.put(key, value);
                logger.info("PUT operation applied: " + key + " = " + value);
                return true;
            case ConsensusUtils.OPERATION_DELETE:
                if (store.containsKey(key)) {
                    store.remove(key);
                    logger.info("DELETE operation applied: " + key);
                    return true;
                } else {
                    logger.warn("DELETE operation failed: Key not found - " + key);
                    return false;
                }
            case ConsensusUtils.OPERATION_GET:
                // Note: GET doesn't alter the state, just retrieves the value.
                if (store.containsKey(key)) {
                    logger.info("GET operation retrieved: " + key + " = " + store.get(key));
                } else {
                    logger.warn("GET operation failed: Key not found - " + key);
                }
                return store.containsKey(key);
            default:
                logger.error("Unknown operation type: " + operation.getOperationType());
                return false;
        }
    }
}
