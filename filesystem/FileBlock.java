package filesystem;

import java.io.IOException;
import java.io.Serializable;

import commons.AddressToIPPort;
import communication.Communicator;
import communication.Message;
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
	FileBlock(String blockName, String[] dataNodeLocations){
		this.blockName = blockName;
		this.dataNodeLocations = dataNodeLocations;
		validLocations = dataNodeLocations.length;
	}
	public void addNodeLocation(String node) throws Exception
	{
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
		
		for(String nodeLocation: dataNodeLocations)
		{
			String[] ipPort;
			try {
				ipPort = AddressToIPPort.addressToIPPort(nodeLocation);
				Message inputMessage = new Message("remove");
				inputMessage.fileName = blockName;
				
				Message returnMessage = Communicator.sendAndReceiveMessage(ipPort[0], Integer.parseInt(ipPort[1]), inputMessage);
				//TODO handle failure from return message
			} catch (NumberFormatException | ClassNotFoundException | InterruptedException | IOException e) {
				// TODO delete
				e.printStackTrace();
			}
			
			
		}
		
	}
	
}
