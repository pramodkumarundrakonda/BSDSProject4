package network;

import consensus.state.Operation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import common.CustomLogger;
import consensus.participant.ConsensusAcceptor;
import consensus.participant.ConsensusLearner;
import consensus.participant.ConsensusProposer;

/**
 * Implements the ServerInterface and handles remote interactions for Paxos protocol.
 */
public class ServerCommunicator extends UnicastRemoteObject implements ServerInterface {

    private ConsensusProposer proposer;
    private ConsensusAcceptor acceptor;
    private ConsensusLearner learner;
    private CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);

    /**
     * Constructs a ServerCommunicator with necessary Paxos role instances.
     * @param proposer The proposer component.
     * @param acceptor The acceptor component.
     * @param learner The learner component.
     * @throws RemoteException if a remote error occurs.
     */
    public ServerCommunicator(ConsensusProposer proposer, ConsensusAcceptor acceptor, ConsensusLearner learner) throws RemoteException {
        this.proposer = proposer;
        this.acceptor = acceptor;
        this.learner = learner;
    }

    @Override
    public boolean prepare(int proposalId, String proposerId) throws RemoteException {
        logger.info("Received prepare request: Proposal ID " + proposalId + " from Proposer " + proposerId);
        return acceptor.prepare(proposalId, proposerId);
    }

    @Override
    public boolean accept(int proposalId, Operation operation) throws RemoteException {
        logger.info("Received accept request: Proposal ID " + proposalId + " for Operation " + operation);
        return acceptor.accept(proposalId, operation);
    }

    @Override
    public boolean commit(Operation operation) throws RemoteException {
        logger.info("Received commit request for Operation " + operation);
        return learner.learn(operation);
    }

    @Override
    public String getValue(String key) throws RemoteException {
        logger.info("Received get value request for key: " + key);
        // Implementation of getValue should be handled by the learner or a direct read from the state store
        return learner.getStateStore().get(key);
    }
}
