package namenode;

// unique identifier for a particular file block

class BlockLocationPair{
	// unique name for the block
	String blockName;
	// location where it exists
	String nodeLocation;
	
	BlockLocationPair(String blockName, String nodeLocation){
		this.blockName = blockName;
		this.nodeLocation = nodeLocation;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj instanceof BlockLocationPair){
        	BlockLocationPair blp = (BlockLocationPair) obj;
            return blockName.equals(blp.blockName) && nodeLocation.equals(blp.nodeLocation);
        }
        else
            return false;
    }
};