package filesystem;

import java.io.Serializable;

import namenode.NameNode;
import commons.Logger;
import conf.Constants;

public class FileBlock implements Serializable{
	
	private static final long serialVersionUID = -1942709905910279925L;
	String blockName;
	long blockSize;
	String[] dataNodeLocations;
	int validLocations;
	int blockLocations;
	
	public FileBlock(String blockName){
		this.blockName = blockName;
		this.dataNodeLocations = new String[Constants.REPLICATION_FACTOR];
		validLocations = 0;
	}
	
	public void addNodeLocation(String node) throws Exception {
		if(this.validLocations>=Constants.REPLICATION_FACTOR)
			throw new Exception("Too many nodes. Exceeds replication factor.");
		dataNodeLocations[validLocations++] = node;
	}
	
	public String[] getNodeLocations(){
		return dataNodeLocations;
	}
	public String getBlockFileName(){
		return blockName;
	}
	
	public void delete() {
		Logger.log("deleting block: " + blockName);
		
		for(String nodeLocation: dataNodeLocations){
			NameNode.deleteThread.push(blockName, nodeLocation);
		}
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof FileBlock){
        	FileBlock fb = (FileBlock)obj;
        	//TODO do we need to compare filelocations too?
            return fb.blockName.equals(blockName);
        }
        else
            return false;
    }

	public void setSize(long size) {
		blockSize = size;
	}
	
	public long getSize() {
		return blockSize;
	}
	
}
