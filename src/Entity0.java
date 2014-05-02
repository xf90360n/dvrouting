public class Entity0 extends Entity {
	int self = 0;
	private int[] directlyConnectedNeighborDistance = new int[NetworkSimulator.NUMENTITIES];
	private int[] minCost = new int[NetworkSimulator.NUMENTITIES];

	// Perform any necessary initialization in the constructor
	public Entity0() {
		directlyConnectedNeighborDistance[1] = 1;
		directlyConnectedNeighborDistance[2] = 3;
		directlyConnectedNeighborDistance[3] = 7;

		// initialization
		for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
			for (int j = 0; j < NetworkSimulator.NUMENTITIES; j++)
				this.distanceTable[i][j] = 999;

		this.distanceTable[0][0] = 0;// this is dumb, but I want to put this in
										// to see how it works
		this.distanceTable[1][0] = 1;// to 1 via 0
		this.distanceTable[2][0] = 3;// to 2 via 0
		this.distanceTable[3][0] = 7;// to 3 via 0

		minCost[0] = 0;
		minCost[1] = 1;
		minCost[2] = 3;
		minCost[3] = 7;

		// A method to handle all packets sending
		sendPacketToNeighbor();

	}

	private void sendPacketToNeighbor() {
		Packet packetToNeighborOne = new Packet(0, 1, minCost);
		Packet packetToNeighborTwo = new Packet(0, 2, minCost);
		Packet packetToNeighborThree = new Packet(0, 3, minCost);

		NetworkSimulator.toLayer2(packetToNeighborOne);
		NetworkSimulator.toLayer2(packetToNeighborTwo);
		NetworkSimulator.toLayer2(packetToNeighborThree);
	}

	// Handle updates when a packet is received. Students will need to call
	// NetworkSimulator.toLayer2() with new packets based upon what they
	// send to update. Be careful to construct the source and destination of
	// the packet correctly. Read the warning in NetworkSimulator.java for more
	// details.
	public void update(Packet p) {
		int source = p.getSource();
		int destination = p.getDest();
		int newMinCost[] = new int[NetworkSimulator.NUMENTITIES];
		boolean ifChanged = false;

		// make a local copy of minCost from packet
		if ((destination == self)) {
			for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
				newMinCost[i] = p.getMincost(i);

			// Comparing and updating the table
			for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++) {

				if (this.distanceTable[i][source] > (newMinCost[i] + directlyConnectedNeighborDistance[source])) {
					this.distanceTable[i][source] = newMinCost[i]
							+ directlyConnectedNeighborDistance[source];
					// this.distanceTable[i][source] = minCost[i];

					// ifChanged = true;
				}

				if (minCost[i] > this.distanceTable[i][source]) {
					minCost[i] = this.distanceTable[i][source];
					ifChanged = true;
				}

			}
			for (int i = 0; i < 4; i++) {
				if (minCost[i] == this.distanceTable[i][source]
						&& newMinCost[i] > minCost[i]) {
					this.distanceTable[i][source] = newMinCost[i]
							+ this.distanceTable[self][source];
					minCost[i] = Math
							.min(Math.min(this.distanceTable[i][0],
									this.distanceTable[i][1]), Math.min(
									this.distanceTable[i][2],
									this.distanceTable[i][3]));

					ifChanged = true;
				}

			}

		}

		if (ifChanged) {
			sendPacketToNeighbor();
		}
	}

	public void linkCostChangeHandler(int whichLink, int newCost) {
		System.out.println("==================Making Changes===============");
		directlyConnectedNeighborDistance[whichLink] = newCost;
		this.distanceTable[whichLink][self] = newCost;
		this.distanceTable[whichLink][whichLink] = newCost;
		for (int i = 0; i < 4; i++)
			this.distanceTable[i][whichLink] = this.distanceTable[i][whichLink] - 1 + 20;
		for (int i = 0; i < 4; i++)
			minCost[i] = Math.min(Math.min(this.distanceTable[i][0],
					this.distanceTable[i][1]), Math.min(
					this.distanceTable[i][2], this.distanceTable[i][3]));
		sendPacketToNeighbor();

	}

	// I modified this print method to see a more detailed table
	public void printDT() {
		System.out.println();
		System.out.println("           via");
		System.out.println(" D0 |   0   1   2   3");
		System.out.println("----+------------");
		for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++) {
			if (i != self) {
				System.out.print("   " + i + "|");
				for (int j = 0; j < NetworkSimulator.NUMENTITIES; j++) {
					if (distanceTable[i][j] < 10) {
						System.out.print("   ");
					} else if (distanceTable[i][j] < 100) {
						System.out.print("  ");
					} else {
						System.out.print(" ");
					}

					System.out.print(distanceTable[i][j]);
				}
				System.out.println();
			}
		}
	}
}
