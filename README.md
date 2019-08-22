The Course was taught by Prof.Steve Ko and all the required scripts for Programming Assignments (PAs) to set up the AVDs in android studio and grading scripts are available at : https://cse.buffalo.edu/~stevko/courses/cse486/spring19/

# PA1: Simple messenger

In this assignment I have developed a simple messenger app. The goal of this app is to enable two Android devices to send messages to each other.

# PA2A: Group messenger with local persistent key-value table

In this assignment, I have developed a group messenger that can send message to multiple AVDs and store them in permanent key-value storage. For this task, I used a content provider, the provider should store all messages but the abstraction it provides should be a general key-value table. The content provider will store these as files in memory.

# PA2B: Group messenger with TOTAL and FIFO ordering Guarantees

This assignment is an enhancement of the previous assignment where I have added ordering guarantees to my group messenger. The guarantees are total ordering as well as FIFO ordering. The messages will be stored in content provider. Failure of the app is also handled in this assignment

PA3: Simple DHT

In this assignment, I have designed a simple DHT based on chord. The 3 main things implemented in this project is 1) ID space partitioning/re-partitioning 2) Ring-based routing 3) Node joins

PA4: Replicated Key-Value Storage - Amazon Dynamo

The main goal of this assignment is to provide availability and linearizability at same time. The implementation provides both read and write operations successfully even under failures keeping in track that a read operation always returns the most recent write. Partitioning and re-partitioning is also carried out the same way Dynamo does. The 4 main tasks carried out in this assignment are,
1) Membership - like dynamo, every node knows every other node
2) Request routing - unlike chord, each dynamo node knows all other nodes in the system and also exactly which partition belongs to which node
3) Quorum replication - for linearlizability, implemented a quorum based replication used by dynamo
4) Failure handling - like dynamo, each the nodes should partition/repartition and handle failures