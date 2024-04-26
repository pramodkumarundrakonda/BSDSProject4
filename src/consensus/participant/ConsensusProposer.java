package consensus.participant;

import common.CustomLogger;
import consensus.state.Operation;
import consensus.state.StateStore;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsensusProposer implements Runnable {
    private static final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);
    private static final AtomicInteger proposalIdCounter = new AtomicInteger(0);

    private final StateStore stateStore;
    private final int serverPort;
    private final List<ConsensusAcceptor> acceptors;
    private final int quorumSize;

    private final ConsensusLearner learner;

    public ConsensusProposer(StateStore stateStore, int serverPort, List<ConsensusAcceptor> acceptors, ConsensusLearner learner) {
        this.stateStore = stateStore;
        this.serverPort = serverPort;
        this.acceptors = acceptors;
        this.learner = learner;
        this.quorumSize = acceptors.size() / 2 + 1; // Majority
    }

    public boolean propose(Operation operation) {
        int proposalId = generateUniqueProposalId();
        logger.info("Proposer on port " + serverPort + " starting proposal with ID: " + proposalId);

        if (sendPrepareRequests(proposalId)) {
            if (sendAcceptRequests(proposalId, operation)) {
                logger.info("Consensus reached on proposal ID: " + proposalId + " for operation: " + operation);
                return true;
            }
        }
        logger.warn("Failed to reach consensus on proposal ID: " + proposalId);
        return false;
    }

    private int generateUniqueProposalId() {
        return proposalIdCounter.incrementAndGet() + serverPort;
    }

    private boolean sendPrepareRequests(int proposalId) {
        int promiseCount = 0;
        for (ConsensusAcceptor acceptor : acceptors) {
            try {
                if (acceptor.prepare(proposalId, "Proposer@" + serverPort)) {
                    promiseCount++;
                    if (promiseCount >= quorumSize) {
                        logger.info("Majority of promises received for proposal ID: " + proposalId);
                        return true;
                    }
                }
            } catch (Exception e) {
                logger.error("Error sending prepare request to acceptor: " + e.getMessage());
            }
        }
        return false;
    }

    private boolean sendAcceptRequests(int proposalId, Operation operation) {
        int acceptanceCount = 0;
        for (ConsensusAcceptor acceptor : acceptors) {
            try {
                if (acceptor.accept(proposalId, operation)) {
                    acceptanceCount++;
                    if (acceptanceCount >= quorumSize) {
                        logger.info("Majority of acceptances received for proposal ID: " + proposalId);

                    }
                }
            } catch (Exception e) {
                logger.error("Error sending accept request to acceptor: " + e.getMessage());
            }
        }
        if (learner != null) {
            try {
                learner.learn(operation);  // Learner commits the operation
                return true;
            } catch (Exception e) {
                logger.error("Error notifying learner: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void run() {
    }
}
