package server;

import consensus.participant.ConsensusAcceptor;
import consensus.participant.ConsensusLearner;
import consensus.participant.ConsensusProposer;
import consensus.state.StateStore;
import network.ServerCommunicator;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

public class ServerNode {
    private ServerCommunicator serverCommunicator;
    private final int serverPort;
    private Thread acceptorThread;

    public ServerNode(int serverPort) {
        this.serverPort = serverPort;
    }

    public void startServer() {
        StateStore stateStore = new StateStore();
        ConsensusLearner learner = new ConsensusLearner(stateStore, serverPort);
        ConsensusAcceptor acceptor = new ConsensusAcceptor(stateStore, serverPort, learner);
        List<Integer> allLearnerAddresses = new ArrayList<>();

        try {
            if (serverCommunicator == null) {
                // Initialize components only once
                List<ConsensusAcceptor> acceptors = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {

                    acceptors.add(new ConsensusAcceptor(stateStore, 5000 + i, new ConsensusLearner(stateStore, 5000 + i)));
                    allLearnerAddresses.add(5000 + i);
                }
                ConsensusProposer proposer = new ConsensusProposer(stateStore, serverPort, acceptors, learner);


                serverCommunicator = new ServerCommunicator(proposer, acceptor, null, allLearnerAddresses);

                learner.setServerCommunicator(serverCommunicator);
                serverCommunicator.setLearner(learner);
            }

            try {
                // Attempt to export the server communicator
                ServerCommunicator stub = (ServerCommunicator) UnicastRemoteObject.exportObject(serverCommunicator, 0);
                Registry registry = LocateRegistry.createRegistry(serverPort);
                registry.rebind("PaxosServer", stub);
            } catch (ExportException e) {
                //System.err.println("Server communicator already exported, binding to existing registry instead.");
                Registry registry = LocateRegistry.getRegistry(serverPort);
                registry.rebind("PaxosServer", serverCommunicator);
            }

            // Start acceptor in its own thread
            acceptorThread = new Thread(acceptor);
            acceptorThread.start();

            System.out.println("Server started on port " + serverPort + ". Ready to accept requests.");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ServerNode <port number>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        ServerNode server = new ServerNode(port);
        server.startServer();
    }
}
