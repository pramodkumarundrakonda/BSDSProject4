package consensus.process;

import consensus.participant.ConsensusProposer;
import consensus.state.Operation;
import common.CustomLogger;
import java.util.concurrent.BlockingQueue;

/**
 * This class encapsulates the runnable process for a proposer in the Paxos consensus algorithm.
 * It is responsible for creating and sending proposals to the system based on input or predefined conditions.
 */
public class ProposerProcess implements Runnable {

    private final ConsensusProposer proposer;
    private final BlockingQueue<Operation> proposalQueue;
    private final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    /**
     * Constructs a ProposerProcess with the proposer's required dependencies.
     * @param proposer The proposer instance that will handle creating and sending proposals.
     * @param proposalQueue A thread-safe queue for receiving operations to propose.
     */
    public ProposerProcess(ConsensusProposer proposer, BlockingQueue<Operation> proposalQueue) {
        this.proposer = proposer;
        this.proposalQueue = proposalQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Wait for an operation to arrive in the queue
                Operation operation = proposalQueue.take();
                logger.info("Proposer process received operation to propose: " + operation);

                // Propose the operation and handle the outcome
                boolean success = proposer.propose(operation);
                if (success) {
                    logger.info("Proposer successfully proposed and achieved consensus for operation: " + operation);
                } else {
                    logger.warn("Proposer failed to achieve consensus for operation: " + operation);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Proposer process was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interruption status
        }
    }
}
