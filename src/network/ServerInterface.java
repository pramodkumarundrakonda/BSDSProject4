package network;

import consensus.state.Operation;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the remote methods that can be invoked over the network by different components
 * of the Paxos consensus system.
 */
public interface ServerInterface extends Remote {

    /**
     * Asks the server to prepare for a proposal with a given proposal ID.
     * @param proposalId The ID of the proposal.
     * @param proposerId The identifier of the proposer making this request.
     * @return true if the proposal can proceed (promise not to accept lower proposal IDs), false otherwise.
     * @throws RemoteException If there is an issue with remote method invocation.
     */
    boolean prepare(int proposalId, String proposerId) throws RemoteException;

    /**
     * Asks the server to accept a proposed operation.
     * @param proposalId The ID of the proposal.
     * @param operation The operation to be accepted.
     * @return true if the operation is accepted, false otherwise.
     * @throws RemoteException If there is an issue with remote method invocation.
     */
    boolean accept(int proposalId, Operation operation) throws RemoteException;

    /**
     * Commits an operation that has been accepted by a quorum.
     * @param operation The operation to commit.
     * @return true if the operation is successfully committed, false otherwise.
     * @throws RemoteException If there is an issue with remote method invocation.
     */
    boolean commit(Operation operation) throws RemoteException;

    /**
     * Retrieves a value for a specified key from the key-value store.
     * This method is part of the read-only operations that might not go through the Paxos process.
     * @param key The key whose value is to be retrieved.
     * @return The value associated with the key, or null if the key does not exist.
     * @throws RemoteException If there is an issue with remote method invocation.
     */
    String getValue(String key) throws RemoteException;

}
