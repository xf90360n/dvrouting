public class Entity3 extends Entity
{    
	int self = 3;
	private int[] directlyConnectedNeighborDistance = new int[NetworkSimulator.NUMENTITIES];
	private int[] minCost = new int[NetworkSimulator.NUMENTITIES];
	
    // Perform any necessary initialization in the constructor
    public Entity3()
    {
    	directlyConnectedNeighborDistance[0] = 7;
    	directlyConnectedNeighborDistance[2] = 2;
    	
    	for(int i=0; i<NetworkSimulator.NUMENTITIES;i++)
    		for(int j=0;j<NetworkSimulator.NUMENTITIES;j++)
    			this.distanceTable[i][j]=999;//initialization
    	
    	this.distanceTable[0][3] = 7;
    	this.distanceTable[1][3] = 999;
    	this.distanceTable[2][3] = 2;
    	this.distanceTable[3][3] = 0;
    	
    	minCost[0] = 7;
    	minCost[1] = 999;
    	minCost[2] = 2;
    	minCost[3] = 0;
    	
    	sendPacketToNeighbor();
    }
    
    private void sendPacketToNeighbor(){
    	Packet packetToNeighborZero = new Packet(3, 0, minCost);
    	Packet packetToNeighborTwo = new Packet(3, 2, minCost);
    	
    	NetworkSimulator.toLayer2(packetToNeighborZero);
    	NetworkSimulator.toLayer2(packetToNeighborTwo);
    }
    
    
    
    // Handle updates when a packet is received.  Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update.  Be careful to construct the source and destination of
    // the packet correctly.  Read the warning in NetworkSimulator.java for more
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
    
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
    }
    
    public void printDT()
    {
        System.out.println("         via");
        System.out.println(" D3 |   0   1   2   3");
        System.out.println("----+--------");
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