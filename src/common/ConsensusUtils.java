package common;

/**
 * Utility class providing helper methods and constants for the consensus system.
 */
public class ConsensusUtils {

    // Constants for operation types
    public static final String OPERATION_PUT = "PUT";
    public static final String OPERATION_GET = "GET";
    public static final String OPERATION_DELETE = "DELETE";

    // Default values for timeouts and retries
    public static final int DEFAULT_TIMEOUT_MS = 3000; // default timeout in milliseconds
    public static final int DEFAULT_RETRY_ATTEMPTS = 3; // default number of retry attempts

    /**
     * Generates a unique proposal ID based on the current time and the server port.
     * @param serverPort The port of the server generating the proposal ID.
     * @return A unique proposal ID.
     */
    public static int generateProposalId(int serverPort) {
        // This is a simple approach. For real systems, you might need a more complex method.
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE + serverPort);
    }

    /**
     * Simulates a random failure based on a given probability.
     * @param failureProbability The probability of failure (0 to 1).
     * @return True if a failure should be simulated, otherwise false.
     */
    public static boolean simulateRandomFailure(double failureProbability) {
        return Math.random() < failureProbability;
    }

    /**
     * Sleeps the current thread for a specified duration.
     * @param durationMs The duration to sleep in milliseconds.
     */
    public static void sleepThread(int durationMs) {
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // You might want to log this or handle it according to your requirements.
        }
    }
}
