package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * ServerStarter initializes and starts multiple ServerNode instances on predefined ports.
 */
public class ServerStarter {

    private List<ServerNode> servers = new ArrayList<>();
    private static final int[] ports = {5001, 5002, 5003, 5004, 5005}; // Predefined ports

    /**
     * Constructor for ServerStarter.
     */
    public ServerStarter() {
    }

    /**
     * Initializes and starts server nodes on the specified ports.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void startServers() throws RemoteException {
        for (int port : ports) {
            try {
                LocateRegistry.createRegistry(port);
                System.out.println("RMI registry created at port " + port);
            } catch (RemoteException e) {
                System.err.println("RMI registry already exists at port " + port);
                // Alternatively, connect to the existing registry:
                // LocateRegistry.getRegistry(port);
            }

            ServerNode server = new ServerNode(port);
            servers.add(server);
            server.startServer();
            System.out.println("Server started on port " + port);
        }
    }

    /**
     * Main method to start the servers.
     * @param args Command line arguments are not used in this configuration.
     */
    public static void main(String[] args) {
        ServerStarter starter = new ServerStarter();
        try {
            starter.startServers();
        } catch (RemoteException e) {
            System.err.println("Error starting servers: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
