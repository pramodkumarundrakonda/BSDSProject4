# Makefile for compiling and running Paxos client and server

# Java compiler
JC = javac

# Java runtime
JVM = java

# Source files
SERVER_SRC = src/server/*.java src/consensus/*.java src/network/*.java src/common/*.java
CLIENT_SRC = src/client/*.java src/network/*.java src/common/*.java

# Output directories
BUILD_DIR = build
SERVER_BUILD = $(BUILD_DIR)/server
CLIENT_BUILD = $(BUILD_DIR)/client
LOGS_DIR = logs

# Create build directories
$(shell mkdir -p $(SERVER_BUILD))
$(shell mkdir -p $(CLIENT_BUILD))
$(shell mkdir -p $(LOGS_DIR))

# Main classes
SERVER_MAIN = server.ServerStarter
CLIENT_MAIN = client.ClientApp

# Classpath
CLASSPATH = .:$(SERVER_BUILD):$(CLIENT_BUILD)

# Compilation flags
JFLAGS = -classpath $(CLASSPATH) -d $(BUILD_DIR)

# Targets
all: server client

server:
	$(JC) $(JFLAGS) $(SERVER_SRC)

client:
	$(JC) $(JFLAGS) $(CLIENT_SRC)

run-server:
	$(JVM) -classpath $(CLASSPATH) $(SERVER_MAIN)

run-client:
	$(JVM) -classpath $(CLASSPATH) $(CLIENT_MAIN)

clean:
	rm -rf $(BUILD_DIR) $(LOGS_DIR)

.PHONY: all server client run-server run-client clean
