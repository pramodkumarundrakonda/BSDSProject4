package consensus.participant;

import common.CustomLogger;
import common.ConsensusUtils;
import consensus.state.Operation;
import consensus.state.StateStore;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsensusProposer implements Runnable {

    private static final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    // Atomic integer to generate unique proposal IDs across threads safely
    private static final AtomicInteger proposalIdGenerator = new AtomicInteger(0);

    private final StateStore stateStore;
    private final int serverPort;
    private final List<ConsensusAcceptor> acceptors;
    private final int quorumSize;

    public ConsensusProposer(StateStore stateStore, int serverPort, List<ConsensusAcceptor> acceptors) {
        this.stateStore = stateStore;
        this.serverPort = serverPort;
        this.acceptors = acceptors;
        this.quorumSize = acceptors.size() / 2 + 1; // Majority of acceptors
    }

    public boolean propose(Operation operation) {
        int proposalId = proposalIdGenerator.incrementAndGet() + serverPort;
        int promisesReceived = 0;

        // Prepare phase
        for (ConsensusAcceptor acceptor : acceptors) {
            if (acceptor.prepare(proposalId, "Proposer@" + serverPort)) {
                promisesReceived++;
                logger.info("Proposer on port " + serverPort + " received a promise from an acceptor.");
            }

            if (promisesReceived >= quorumSize) {
                logger.info("Proposer on port " + serverPort + " received enough promises, moving to accept phase.");
                break;
            }
        }

        if (promisesReceived < quorumSize) {
            logger.warn("Proposer on port " + serverPort + " failed to receive majority promises.");
            return false; // Not enough promises to proceed
        }

        // Accept phase
        int acceptancesReceived = 0;
        for (ConsensusAcceptor acceptor : acceptors) {
            if (acceptor.accept(proposalId, operation)) {
                acceptancesReceived++;
                logger.info("Proposer on port " + serverPort + " received acceptance from an acceptor.");
            }

            if (acceptancesReceived >= quorumSize) {
                logger.info("Proposer on port " + serverPort + " received enough acceptances, committing the operation.");
                return stateStore.applyOperation(operation);
            }
        }

        logger.warn("Proposer on port " + serverPort + " did not receive enough acceptances.");
        return false; // Not enough acceptances
    }

    @Override
    public void run() {
        // The run method would be used to propose operations periodically or based on some criteria
    }
}
