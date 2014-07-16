package namenode;
class BlockLocationPair{
	String blockName;
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