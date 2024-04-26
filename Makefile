# Makefile for compiling and running Paxos client and server

# Java compiler
JC = javac

# Java runtime
JVM = java


# Base directory for source files
SRC_DIR = src


# Packages
SERVER_PKG = $(SRC_DIR)/server
CLIENT_PKG = $(SRC_DIR)/client
CONSENSUS_PKG = $(SRC_DIR)/consensus
CONSENSUS_PARTICIPANT_PKG = $(CONSENSUS_PKG)/participant
CONSENSUS_PROCESS_PKG = $(CONSENSUS_PKG)/process
CONSENSUS_STATE_PKG = $(CONSENSUS_PKG)/state
NETWORK_PKG = $(SRC_DIR)/network
COMMON_PKG = $(SRC_DIR)/common

# logs dir
LOGS_DIR = logs

# Output directory
BUILD_DIR = build

# Java files
SERVER_FILES = $(wildcard $(SERVER_PKG)/*.java)
CLIENT_FILES = $(wildcard $(CLIENT_PKG)/*.java)
CONSENSUS_FILES = $(wildcard $(CONSENSUS_PKG)/*.java) \
                  $(wildcard $(CONSENSUS_PARTICIPANT_PKG)/*.java) \
                  $(wildcard $(CONSENSUS_PROCESS_PKG)/*.java) \
                  $(wildcard $(CONSENSUS_STATE_PKG)/*.java)
NETWORK_FILES = $(wildcard $(NETWORK_PKG)/*.java)
COMMON_FILES = $(wildcard $(COMMON_PKG)/*.java)

# Classpath
CLASSPATH = .:$(BUILD_DIR)

# Main classes
SERVER_MAIN = server.ServerStarter
CLIENT_MAIN = client.ClientApp

# Compilation flags
JFLAGS = -classpath $(CLASSPATH) -d $(BUILD_DIR)

all: server client

server: $(SERVER_FILES) $(CONSENSUS_FILES) $(NETWORK_FILES) $(COMMON_FILES)
	mkdir -p $(BUILD_DIR)
	$(JC) $(JFLAGS) $^

client: $(CLIENT_FILES) $(NETWORK_FILES) $(COMMON_FILES)
	mkdir -p $(BUILD_DIR)
	$(JC) $(JFLAGS) $^


run-server:
	$(JVM) -classpath $(CLASSPATH) $(SERVER_MAIN)

run-client:
	$(JVM) -classpath $(CLASSPATH) $(CLIENT_MAIN) $(PORT)

clean:
	rm -rf $(BUILD_DIR) $(LOGS_DIR)

.PHONY: all server client run-server run-client clean
