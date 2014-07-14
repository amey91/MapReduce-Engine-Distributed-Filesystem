package filesystem;

import java.io.Serializable;

import namenode.NameNode;
import commons.Logger;
import conf.Constants;

public class FileBlock implements Serializable{
	
	private static final long serialVersionUID = -1942709905910279925L;
	String blockName;
	String[] dataNodeLocations;
	int validLocations;
	
	public FileBlock(String blockName){
		this.blockName = blockName;
		this.dataNodeLocations = new String[Constants.REPLICATION_FACTOR];
		validLocations = 0;
	}
	
	public FileBlock(String blockName, String[] dataNodeLocations){
		this.blockName = blockName;
		this.dataNodeLocations = dataNodeLocations;
		validLocations = dataNodeLocations.length;
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
	
}
