
# Java RMI Paxos-Based Fault-Tolerant Key-Value Store (Project 4)

This repository hosts an advanced Java RMI (Remote Method Invocation) client-server application, which extends Project 3 by replacing the two-phase commit protocol with Paxos for enhanced fault tolerance and consistency. This project involves a key-value store replicated across five servers using the Paxos consensus algorithm to handle updates reliably even in the presence of server failures.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Usage](#usage)
    - [Compilation](#compilation)
    - [Running the Servers](#running-the-servers)
    - [Running the Clients](#running-the-clients)
- [File Structure](#file-structure)
- [Dependencies](#dependencies)
- [Concurrency and Thread Safety](#concurrency-and-thread-safety)
- [Configuration](#configuration)
- [Fault Tolerance and Acceptor Failures](#fault-tolerance-and-acceptor-failures)
- [Contributors](#contributors)

## Overview

The Java RMI Client-Server Application is now enhanced with the Paxos consensus algorithm across five servers. The implementation ensures that client requests are processed reliably and state is consistently replicated, even if some of the servers experience failures.

## Features

- **Paxos Consensus Algorithm**: Implements Paxos to ensure that all non-failing nodes can agree on the next state of the replicated key-value store in the presence of server failures.
- **Replication Across Five Servers**: Enhances availability and fault tolerance by replicating the key-value store across multiple servers.
- **Java RMI Protocol**: Utilizes Java RMI for communication between clients and servers, enabling efficient remote method invocation.
- **Concurrency and Thread Safety**: Designed to handle multiple concurrent client requests while ensuring data consistency and integrity.

## Usage

### Compilation

1. Ensure Java Development Kit (JDK) is installed on your system.
2. Compile the source code using the provided Makefile:
   
```bash
make clean all
```

### Running the Servers

The servers are pre-configured to run on specific ports (5001 to 5005) to simplify deployment and minimize setup errors. This static port assignment helps in consistent networking and firewall rules configuration across different environments.

To start the servers, use:

```bash
make run-server
```

This command will start all 5 Paxos servers and initialize them to be ready to form consensus on incoming client requests.

### Running the Clients

To launch a client that connects to the servers:

```bash
make run-client
```

Clients are configured to connect randomly to one of the running servers. This demonstrates the fault tolerance by ensuring that the client can operate correctly even if one of the servers becomes unavailable.

## File Structure

The project is organized into several directories and packages to maintain a clear separation of concerns and improve manageability:

- **src/**
    - **server/**: Contains server-side Java source files implementing the server functionality and Paxos roles (Proposers, Acceptors, Learners).
       
    - **client/**: Contains client-side Java source files for interfacing with the Paxos server cluster.
       
    - **consensus/**: Contains Java classes related to the Paxos algorithm implementation.
        
    - **common/**: Contains shared utilities and helper classes.
       
    - **network/**: Manages network communications aspects for both client and server.


## Dependencies

- **Java RMI**: Required for remote method invocation between clients and servers.
- **Java SDK**: Ensure JDK 11 or newer is installed.

## Concurrency and Thread Safety

The system employs various mechanisms to ensure thread safety and manage concurrency effectively:
- **Synchronized Blocks and Locks**: Protects shared resources and critical sections to prevent race conditions.
- **Atomic Variables**: Uses atomic variables for count and flag management where needed.
- **Thread-Safe Collections**: Utilizes thread-safe variants of collections for storing state and managing data.

## Fault Tolerance and Acceptor Failures
- **Acceptor Failures**: Acceptor threads are designed to "fail" randomly to simulate real-world scenarios where network or server issues cause nodes to temporarily drop out of consensus. Each Acceptor has a mechanism to simulate a crash and subsequent recovery, demonstrating how Paxos can handle node failures gracefully.
- **Recovery Mechanism**: Upon "failure," an Acceptor becomes inactive and stops responding to consensus requests. After a random delay, it restarts, resetting its state and rejoining the consensus process. This ensures that even if a failure occurs, the system remains operational and can continue processing requests once the Acceptor recovers.

## Contributors

- Pramod Kumar Undrakonda
