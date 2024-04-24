package consensus.process;

import consensus.participant.ConsensusAcceptor;
import consensus.state.StateStore;
import common.CustomLogger;
import java.util.concurrent.BlockingQueue;
import consensus.state.Operation;

/**
 * This class encapsulates the runnable process for an acceptor in the Paxos consensus algorithm.
 * It manages the lifecycle of an acceptor and handles its interactions in a separate thread.
 */
public class AcceptorProcess implements Runnable {

    private final ConsensusAcceptor acceptor;
    private final BlockingQueue<Operation> incomingProposals;
    private final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    /**
     * Constructs an AcceptorProcess with a given state store and communication queue.
     * @param stateStore The state store to be used by the acceptor.
     * @param serverPort The port number associated with this acceptor, used for identification.
     * @param incomingProposals A thread-safe queue for incoming proposal operations.
     */
    public AcceptorProcess(StateStore stateStore, int serverPort, BlockingQueue<Operation> incomingProposals) {
        this.acceptor = new ConsensusAcceptor(stateStore, serverPort);
        this.incomingProposals = incomingProposals;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Operation operation = incomingProposals.take();  // This blocks until an operation is available
                logger.info("Acceptor at port " + acceptor.getServerPort() + " received operation: " + operation);

                boolean prepared = acceptor.prepare(operation.getProposalId(), "ProposerIdentifer");
                if (prepared) {
                    boolean accepted = acceptor.accept(operation.getProposalId(), operation);
                    if (accepted) {
                        logger.info("Operation accepted: " + operation);
                    } else {
                        logger.warn("Operation not accepted: " + operation);
                    }
                } else {
                    logger.warn("Preparation failed for operation: " + operation);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Acceptor process interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interruption status
        }
    }

}
