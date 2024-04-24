package consensus.participant;

import common.CustomLogger;
import consensus.state.Operation;
import consensus.state.StateStore;

public class ConsensusLearner implements Runnable {

    private static final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    private final StateStore stateStore;
    private final int serverPort;

    public ConsensusLearner(StateStore stateStore, int serverPort) {
        this.stateStore = stateStore;
        this.serverPort = serverPort;
    }

    public StateStore getStateStore() {
        return this.stateStore;
    }

    /**
     * Learns and commits the operation to the store.
     * This method should be called once a consensus has been reached and an operation accepted by the quorum.
     *
     * @param operation The operation to be committed.
     * @return true if the operation was successfully committed, false otherwise.
     */
    public boolean learn(Operation operation) {
        // Committing the operation
        boolean isCommitted = stateStore.applyOperation(operation);
        if (isCommitted) {
            logger.info("Learner on port " + serverPort + " has committed the operation: " + operation);
        } else {
            logger.warn("Learner on port " + serverPort + " failed to commit the operation: " + operation);
        }
        return isCommitted;
    }

    @Override
    public void run() {
        // This could be implemented to listen for consensus results in a real system
        // For demonstration, assume operations are pushed to this method via other mechanisms
    }
}
