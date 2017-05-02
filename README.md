# P2P-Applicaiton
This is my Network Communication Assignment. The objective of this assignment is to implement interaction between hosts.
For this assigment, it requires the following:
1. The concurrent directory server program runs waiting for a connection from a client.
2. The clients and the server should (in general) run on different machines.
3. Each client can send three different meassages to the direcory server -- "JOIN", "LEAVE", and "LIST". "JOIN" and "LEAVE" are for joining and leaving the list appropriate actions. The server needs to be concurrent because multiple clients should be able to talk to it simultaneously. "LIST" message should return all info such as IP address, listening port# of the peer etc, so peer can be contacted direcly.
When the program quits, the peer programs should close the sockets.
