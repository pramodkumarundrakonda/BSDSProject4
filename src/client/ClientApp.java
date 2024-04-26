package client;

import network.ServerInterface;
import consensus.state.Operation;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class ClientApp {

    private ServerInterface server;

    public ClientApp(String host, int port) {
        //Random random = new Random();
        //int port = 5001 + random.nextInt(5); // Randomly selects a port from 5001 to 5005
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (ServerInterface) registry.lookup("PaxosServer");
            System.out.println("Connected to server at " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void populateKeyValueStore() {
        System.out.println("-------------------Initializing the key-value store with 10 pairs---------------");
        for (int i = 1; i <= 10; i++) {
            String key = "key" + i;
            sendRequest("PUT", key, String.valueOf(i));
        }
        System.out.println("---------------Initialization Done !!! -------------------------------------------");
    }

    public void performOperations() {
        System.out.println("---------------Performing 5 PUT/GET/DELETE Operations ------------------------");
        for (int i = 11; i <= 15; i++) {
            String key = "key" + i;
            sendRequest("PUT", key, String.valueOf(i));
            sendRequest("GET", key, null);
            sendRequest("DELETE", key, null);
        }
    }

    public void sendRequest(String operationType, String key, String value) {
        try {
            Operation operation = new Operation(0, operationType, key, value);
            if (operationType.equalsIgnoreCase("GET")) {
                String result = server.getValue(key);
                System.out.println("GET result for " + key + ": " + result);
            } else {
                boolean success = server.propose(operation);
                System.out.println(operationType + " operation " + (success ? "succeeded" : "failed") + " for " + key);
            }
        } catch (Exception e) {
            System.err.println("Error during remote operation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1 ) {
            System.out.println("Usage: java ClientApp <port>");
            return;
        }
        ClientApp client = new ClientApp("localhost", Integer.parseInt(args[0]));
        UserInterface ui = new UserInterface(client);
        ui.start();
    }

}
