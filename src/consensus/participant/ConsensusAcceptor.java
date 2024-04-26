package consensus.participant;

import common.CustomLogger;
import consensus.state.Operation;
import consensus.state.StateStore;
import java.util.Random;

public class ConsensusAcceptor implements Runnable {

    private static final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    private volatile int highestProposalIdReceived = 0;
    private volatile String lastAcceptedValue = null;
    private final StateStore stateStore;

    private final int serverPort;
    private final Random random = new Random();
    private boolean active = true;

    private ConsensusLearner learner;

    public ConsensusAcceptor(StateStore stateStore, int serverPort, ConsensusLearner learner) {
        this.stateStore = stateStore;
        this.serverPort = serverPort;
        this.learner = learner;
    }


    public int getServerPort() {
        return serverPort;
    }


    // Method to respond to prepare requests
    public synchronized boolean prepare(int proposalId, String proposerIdentifier) {
        if (!active) return false;

        logger.info("Acceptor on port " + serverPort + " received prepare request from " + proposerIdentifier);
        if (proposalId > highestProposalIdReceived) {
            highestProposalIdReceived = proposalId;
            logger.info("Acceptor on port " + serverPort + " promises not to accept proposals lower than " + proposalId);
            return true;
        }
        return false;
    }

    // Method to respond to accept requests
    public synchronized boolean accept(int proposalId, Operation operation) {
        if (!active) return false;

        if (proposalId >= highestProposalIdReceived) {
            highestProposalIdReceived = proposalId;
            logger.info("Acceptor on port " + serverPort + " accepts the proposal: " + operation);
            return true;
        } else {
            logger.warn("Acceptor on port " + serverPort + " rejects the proposal as it has a lower id than " + highestProposalIdReceived);
            return false;
        }
    }

    // Method to simulate failures and restarts
    private void simulateFailureAndRestart() throws InterruptedException {
        // Simulating normal operation
        Thread.sleep(random.nextInt(3000) + 2000); // Simulate longer operational periods (2 to 5 seconds)

        if (random.nextDouble() < 0.02) { // 2% chance of failure, less frequent
            logger.warn("Acceptor on port " + serverPort + " failing.");
            active = false; // Simulate acceptor being inactive

            // Simulate downtime during failure
            Thread.sleep(random.nextInt(60000) + 30000); // Failures last between 30 to 90 seconds

            // Restart and start afresh
            active = true; // Simulate acceptor being active again
            highestProposalIdReceived = 0; // Reset state for a new start
            lastAcceptedValue = null; // Reset the last accepted value
            logger.info("Acceptor on port " + serverPort + " restarted.");
        }
    }


    // Runnable method where the Acceptor's behavior is defined
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                simulateFailureAndRestart();
                // Include additional logic for responding to Paxos messages if needed
            } catch (InterruptedException e) {
                logger.error("Acceptor on port " + serverPort + " interrupted." + e.getMessage());
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
