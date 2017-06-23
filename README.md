# CAN

CAN implementation (only node insertion) for Peersim, with consideration of node's energy
When a new node tries to join the network, if a node will miss energy soon, the new one takes his zone and information.

To compile the program use make
To run the program use make run

Configuration file :
Parameter SIZE must be 1 because the network is initialized with only one node. CANEvent creates the events (node creation and message JOIN from it)
Parameter simulation.endtime can be modify to let more (or less) nodes be created and join the network


Some functions are implemented but not used yet
