# txn-serializability-checker
This project assesses transaction schedule serializability in databases. It reads schedules, constructs precedence graphs, detects cycles, and checks for conflict serializability using topological sorting.

## Overview

The purpose of this program is to assess the conflict serializability of transaction schedules, which is a fundamental concept in ensuring the consistency and isolation properties of transactions in database systems. The tool reads a text file containing a series of database operations and determines if the interleaved transaction schedule can be serialized without conflict, ensuring that the schedule maintains database consistency.

## Technical Aspects

At the core of this implementation is the logic to construct precedence graphs from a given schedule of database operations and to assess the graph for cycles which would indicate a non-serializable schedule.

### Features

- **Transaction Operation Parsing**: Reads schedules from text files, parsing operations by transaction IDs and data items.
- **Precedence Graph Construction**: Builds a graph representing the dependencies between transactions.
- **Cycle Detection**: Identifies cycles in the graph which imply a non-serializable schedule.
- **Topological Sorting**: If serializable, provides a topological order for the transactions.
- **Random Schedule Generation**: Produces randomly interleaved schedules of transactions for analysis.

### Algorithm

The main functionalities are split into two tasks:
1. **Graph-Based Serialization Checking**: Reads a transaction schedule and constructs a precedence graph to determine conflict serializability.
2. **Random Schedule Serializability Testing**: Generates random transaction schedules and checks them for serializability.

### How to Run:

- `javac -cp /txn-serializability-checker/gson-2.8.2.jar MainClass.java`

- For precedence graph construction and serializability check:
MainClass /path/to/schedule.sch

- For random schedule generation and serializability testing:
MainClass /path/to/transactions.set <NUM_ITERATIONS> <RANGE>