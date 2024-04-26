package network;

import consensus.state.Operation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import common.CustomLogger;
import consensus.participant.ConsensusAcceptor;
import consensus.participant.ConsensusLearner;
import consensus.participant.ConsensusProposer;

/**
 * Implements the ServerInterface and handles remote interactions for the Paxos protocol using RMI.
 */
public class ServerCommunicator extends UnicastRemoteObject implements ServerInterface {

    private ConsensusProposer proposer;
    private ConsensusAcceptor acceptor;
    private ConsensusLearner learner;
    private List<Integer> allLearnerAddresses;  // List of all other learner's RMI registry addresses
    private CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    private BlockingQueue<Operation> acceptedOperationsQueue = new LinkedBlockingQueue<>();

    public ServerCommunicator(ConsensusProposer proposer, ConsensusAcceptor acceptor, ConsensusLearner learner, List<Integer> allLearnerAddresses) throws RemoteException {
        super();
        this.proposer = proposer;
        this.acceptor = acceptor;
        this.learner = learner;
        this.allLearnerAddresses = allLearnerAddresses;
    }

    public void setLearner(ConsensusLearner learner) {
        this.learner = learner;
    }

    @Override
    public boolean propose(Operation operation) throws RemoteException {
        logger.info("Proposing operation: " + operation);
        return proposer.propose(operation);
    }

    @Override
    public boolean prepare(int proposalId, String proposerId) throws RemoteException {
        logger.info("Received prepare request: Proposal ID " + proposalId + " from Proposer " + proposerId);
        return acceptor.prepare(proposalId, proposerId);
    }

    @Override
    public boolean accept(int proposalId, Operation operation) throws RemoteException {
        logger.info("Received accept request: Proposal ID " + proposalId + " for Operation " + operation);
        boolean result = acceptor.accept(proposalId, operation);
        if (result) {
            acceptedOperationsQueue.add(operation); // Add to queue if accepted
        }
        return result;
    }


    @Override
    public boolean commit(Operation operation, int serverPort) throws RemoteException {
        logger.info("Received commit request for Operation " + operation);
        boolean isCommitted = learner.getStateStore().applyOperation(operation);
        if (isCommitted) {
            logger.info("Learner on port " + serverPort + " has committed the operation: " + operation);
        } else {
            logger.warn("Learner on port " + serverPort + " failed to commit the operation: " + operation);
        }
        return isCommitted;
    }

    @Override
    public String getValue(String key) throws RemoteException {
        logger.info("Received get value request for key: " + key);
        return learner.getStateStore().get(key);
    }


    /**
     * Waits for an accepted operation to be available and returns it. This is a blocking method and should be
     * called from a thread that handles operation commitment.
     *
     * @return The next accepted operation.
     * @throws InterruptedException if interrupted while waiting.
     */
    public Operation waitForAcceptedOperation() throws InterruptedException {
        return acceptedOperationsQueue.take();
    }

}
