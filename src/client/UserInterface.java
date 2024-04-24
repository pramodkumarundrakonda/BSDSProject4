package client;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class UserInterface {

    private final ClientApp client;
    private final Scanner scanner;

    public UserInterface(ClientApp client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the Paxos Key-Value Store Client!");
        System.out.println("It looks like this is your first time running the client.");
        System.out.println("Do you want to prepopulate the key-value store? (yes/no)");
        String response = scanner.nextLine();
        if ("yes".equalsIgnoreCase(response)) {
            client.populateKeyValueStore();
            client.performOperations();
        }

        // Regular operation
        boolean running = true;
        while (running) {
            System.out.println("\nEnter operation (PUT/GET/DELETE) followed by key and optionally value:");
            System.out.println("Type 'exit' to quit.");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input.trim())) {
                running = false;
            } else {
                handleOperation(input);
            }
        }

        scanner.close();
        System.out.println("Exiting... Thank you for using the Paxos Key-Value Store Client!");
    }

    private void handleOperation(String input) {
        String[] parts = input.split(" ");
        if (parts.length >= 2) {
            String operation = parts[0];
            String key = parts[1];
            String value = (parts.length > 2) ? parts[2] : null;
            client.sendRequest(operation, key, value);
        } else {
            System.out.println("Invalid operation format.");
        }
    }
}
