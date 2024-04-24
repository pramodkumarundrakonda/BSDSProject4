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

    public ServerNode(int serverPort) {
        this.serverPort = serverPort;
    }

    public void startServer() {
        try {
            if (serverCommunicator == null) {
                // Initialize components only once
                StateStore stateStore = new StateStore();
                ConsensusAcceptor acceptor = new ConsensusAcceptor(stateStore, serverPort);
                List<ConsensusAcceptor> acceptors = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    acceptors.add(new ConsensusAcceptor(stateStore, 5000 + i));  // Example port numbers
                }
                ConsensusProposer proposer = new ConsensusProposer(stateStore, serverPort, acceptors);
                ConsensusLearner learner = new ConsensusLearner(stateStore, serverPort);

                serverCommunicator = new ServerCommunicator(proposer, acceptor, learner);
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
