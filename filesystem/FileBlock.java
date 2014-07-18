package filesystem;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;

import namenode.InvalidDataNodeException;
import namenode.NameNode;
import commons.Logger;
import communication.Communicator;
import communication.Message;
import conf.Constants;

public class FileBlock implements Serializable{
	
	private static final long serialVersionUID = -1942709905910279925L;
	String blockName;
	long blockSize;
	String[] dataNodeLocations;
	int validLocations;
	
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
			NameNode.instance.deleteThread.push(blockName, nodeLocation);
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

	public void fix(String id) throws FileSystemException {
		ArrayList<String> failList = new ArrayList<String>();
		ArrayList<String> doneList = new ArrayList<String>();
		int location = 0, pos = 0;
		for(String s: dataNodeLocations){
			pos++;
			if(s.equals(id)){
				failList.add(s);
				location = pos;
			}
			else
				doneList.add(s);
		}
		try {
			dataNodeLocations[location] = "";
			ArrayList<String> alternate = NameNode.instance.getNewLocations(doneList.get(0), doneList, failList);
			if(alternate ==  null)
				throw new FileSystemException("Fixing the block failed");
			dataNodeLocations[location] = alternate.get(0);
			
			Message m = new Message("sendFile");
			m.fileName = blockName;
			m.sendLocation = dataNodeLocations[location];
			try {
				Socket socket = Communicator.CreateDataSocket(doneList.get(0));
				Message result = Communicator.sendAndReceiveMessage(socket, m);
				socket.close();
				if(result.type.equals("fail"))
					throw new FileSystemException("Fixing the block fail");
					
			} catch (InterruptedException | IOException | ClassNotFoundException e) {
				throw new FileSystemException("Fixing the block failed2");
			} 
		} catch (RemoteException | InvalidDataNodeException e) {
			Logger.log("Can't happen");
			e.printStackTrace();
		}
	}
	
}
