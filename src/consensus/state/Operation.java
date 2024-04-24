package consensus.state;

import java.io.Serializable;

/**
 * Represents a single operation to be performed on the key-value store.
 * This class is designed to be serializable to facilitate easy transmission over a network if necessary.
 */
public class Operation implements Serializable {
    private final String operationType;  // The type of operation (e.g., PUT, GET, DELETE)
    private final String key;            // The key involved in the operation
    private final String value;          // The value involved in the operation (for PUT)

    private final int proposalId;

    /**
     * Constructor for Operation class.
     * @param operationType The type of operation (e.g., "PUT", "GET", "DELETE")
     * @param key The key involved in the operation.
     * @param value The value to associate with the key (used for PUT operations).
     */
    public Operation(int proposalId, String operationType, String key, String value) {
        this.proposalId = proposalId;
        this.operationType = operationType;
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the operation type.
     * @return The operation type.
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Gets the key involved in the operation.
     * @return The key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the value involved in the operation (relevant for PUT).
     * @return The value, or null if not applicable.
     */
    public String getValue() {
        return value;
    }

    public int getProposalId() {
        return proposalId;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "operationType='" + operationType + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
