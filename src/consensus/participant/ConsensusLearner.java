package consensus.participant;

import common.CustomLogger;
import consensus.state.Operation;
import consensus.state.StateStore;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import network.ServerCommunicator;
import network.ServerInterface;

public class ConsensusLearner implements Runnable {

    private static final CustomLogger logger = new CustomLogger(CustomLogger.LogLevel.INFO);
    private final StateStore stateStore;
    private final int serverPort;
    private ServerCommunicator serverCommunicator;

    public ConsensusLearner(StateStore stateStore, int serverPort) {
        this.stateStore = stateStore;
        this.serverPort = serverPort;
    }

    public StateStore getStateStore() {
        return this.stateStore;
    }

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }


    /**
     * Learns and commits the operation to the store.
     * This method should be called once a consensus has been reached and an operation accepted by the quorum.
     */
    public void learn(Operation operation) throws RemoteException {
        // Committing the operation
        boolean isCommitted = stateStore.applyOperation(operation);
        if (isCommitted) {
            logger.info("Learner on port " + serverPort + " has committed the operation: " + operation);
            notifyAllLearners(operation, serverPort);
        } else {
            logger.warn("Learner on port " + serverPort + " failed to commit the operation: " + operation);
        }
        //return isCommitted;
    }

/*    private void notifyOtherLearners(Operation operation) throws RemoteException {


        serverCommunicator.notifyAllLearners(operation, serverPort);
    }*/

    /**
     * Notify all learners about a committed operation to ensure consistency across all servers.
     * @param operation The operation that has been committed.
     * @throws RemoteException if a remote error occurs during notification.
     */
    public void notifyAllLearners(Operation operation, int port) throws RemoteException {
        List<Integer> allLearnerAddresses = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            allLearnerAddresses.add(5000 + i);
        }

        // Notify other learners using RMI
        logger.info("Notifying all learners about the committed operation: " + operation);
        for (int address : allLearnerAddresses) {
            if (address == port){
                continue;
            }
            try {
                String host = "localhost";
                Registry registry = LocateRegistry.getRegistry(host, address);
                ServerInterface learnerStub = (ServerInterface) registry.lookup("PaxosServer");
                learnerStub.commit(operation, address);
            } catch (Exception e) {
                logger.error("Failed to notify learner at " + address + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void run() {

    }
}
