package consensus.participant;

import common.CustomLogger;
import common.ConsensusUtils;
import consensus.state.StateStore;
import consensus.state.Operation;

public class ConsensusAcceptor implements Runnable {

    private static final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    private volatile int highestProposalIdReceived = 0;
    private final StateStore stateStore;
    private final int serverPort;

    public ConsensusAcceptor(StateStore stateStore, int serverPort) {
        this.stateStore = stateStore;
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return this.serverPort; // Assuming serverPort is an instance variable in ConsensusAcceptor
    }


    public synchronized boolean prepare(int receivedProposalId, String proposerIdentifier) {
        logger.info("Acceptor on port " + serverPort + " received prepare request from " + proposerIdentifier);
        if (receivedProposalId > highestProposalIdReceived) {
            highestProposalIdReceived = receivedProposalId;
            logger.info("Acceptor on port " + serverPort + " promises not to accept proposals lower than " + receivedProposalId);
            return true;
        }
        return false;
    }

    public synchronized boolean accept(int proposalId, Operation operation) {
        if (proposalId >= highestProposalIdReceived) {
            highestProposalIdReceived = proposalId;
            logger.info("Acceptor on port " + serverPort + " accepts the proposal: " + operation);
            // Here you can apply the operation to the state
            return stateStore.applyOperation(operation);
        } else {
            logger.warn("Acceptor on port " + serverPort + " rejects the proposal as it has a lower id than " + highestProposalIdReceived);
            return false;
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // Here you can simulate random failures or sleep the acceptor
            if (ConsensusUtils.simulateRandomFailure(0.1)) { // 10% failure probability
                logger.warn("Acceptor on port " + serverPort + " simulating failure.");
                ConsensusUtils.sleepThread(ConsensusUtils.DEFAULT_TIMEOUT_MS);
            }
        }
    }
}
