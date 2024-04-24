package consensus.process;

import consensus.participant.ConsensusLearner;
import consensus.state.Operation;
import common.CustomLogger;
import java.util.concurrent.BlockingQueue;

/**
 * This class encapsulates the runnable process for a learner in the Paxos consensus algorithm.
 * It listens for operations that have been accepted by a majority and commits them to the state.
 */
public class LearnerProcess implements Runnable {

    private final ConsensusLearner learner;
    private final BlockingQueue<Operation> acceptedOperationsQueue;
    private final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    /**
     * Constructs a LearnerProcess with the learner's required dependencies.
     * @param learner The learner instance that will commit operations to the state store.
     * @param acceptedOperationsQueue A thread-safe queue from which the learner receives accepted operations.
     */
    public LearnerProcess(ConsensusLearner learner, BlockingQueue<Operation> acceptedOperationsQueue) {
        this.learner = learner;
        this.acceptedOperationsQueue = acceptedOperationsQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Wait for an operation to arrive in the queue
                Operation operation = acceptedOperationsQueue.take();
                logger.info("Learner process received accepted operation: " + operation);

                // Commit the operation using the learner
                boolean success = learner.learn(operation);
                if (success) {
                    logger.info("Learner successfully committed operation: " + operation);
                } else {
                    logger.warn("Learner failed to commit operation: " + operation);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Learner process was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interruption status
        }
    }
}
