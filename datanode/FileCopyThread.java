package datanode;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedQueue;

import commons.AddressToIPPort;
import commons.Logger;
import communication.Communicator;
import communication.Message;
import conf.Constants;
import filesystem.FileBlock;
import filesystem.FileSystemException;


public class FileCopyThread extends Thread{


	ConcurrentLinkedQueue<SendingEntity> additionQueue 
						= new ConcurrentLinkedQueue<SendingEntity>();
	
	public void add(String localPath, String HDFSFilePath, FileBlock[] fileBlocks, long[] splitSizes){
		DistFile f = new DistFile(HDFSFilePath, fileBlocks, localPath, splitSizes);
		
		for(Block b: f.blocks)
			for(SendingEntity e: b.sendingEntities)
				additionQueue.add(e);
		
	}
	
	@Override
	public void run(){
		while(true){
			try{
				for(SendingEntity e: additionQueue){
					if(processSendingEntity(e))
						additionQueue.remove(e);
				}

			} catch(Exception e){
				Logger.log("Error encountered while deleting distributed file: " + e.getMessage() );
				e.printStackTrace();
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Boolean processSendingEntity(SendingEntity e){
		
		try
		{
			Socket socket [] = new Socket[1]; 
			
			String[] ipPort = AddressToIPPort.addressToIPPort(e.nodeLocation);
			
			Logger.log("sending message: "+ipPort[0] + Integer.parseInt(ipPort[1]));
			socket[0] = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
			Message sendMessage = new Message("add");
			sendMessage.fileName = e.parent.blockName;
			sendMessage.fileSize = e.parent.size;
	
			Communicator.sendMessage(socket[0], sendMessage);
			
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(e.parent.parent.localPath));
			
			bis.skip(e.parent.offset);
			
			long actualSendSize = Communicator.sendStream(socket, bis, e.parent.size);
			bis.close();
			
			return actualSendSize == e.parent.size;
		}catch(IOException | InterruptedException ex){
			return false;
		}
	}
}


class DistFile{
	
	Block[] blocks;
	String HDFSFilePath;
	FileBlock[] fileBlocks;
	long[] result;
	String localPath;
	
	DistFile(String HDFSFilePath, FileBlock[] fileBlocks, String localPath, long[] sizes){
		this.HDFSFilePath = HDFSFilePath;
		this.localPath = localPath;
		blocks = new Block[fileBlocks.length];
		successArray = new Boolean[fileBlocks.length];
		this.fileBlocks = fileBlocks;
		result = sizes;
		
		long offset = 0;
		for(int i=0;i<fileBlocks.length;i++){
			blocks[i] = new Block(fileBlocks[i], this, offset, sizes[i]);
			offset += sizes[i];
			successArray[i] = false;
		}
	}

	Boolean[] successArray = new Boolean[Constants.REPLICATION_FACTOR];
	
	void report(String blockName){
		for(int i=0;i<Constants.REPLICATION_FACTOR;i++)
			if(blocks[i].blockName.equals(blockName))
				successArray[i] = true;
		
		for(Boolean b: successArray)
			if(!b)
				return;
		

		try {
			DataNode.nameNode.confirmLocalToHDFS(DataNode.key, HDFSFilePath, fileBlocks, result);
		} catch (RemoteException | FileSystemException e) {
			// TODO 
			Logger.log(e.getMessage());
			e.printStackTrace();
		}
	}
}

class Block{
	
	SendingEntity[] sendingEntities = new SendingEntity[Constants.REPLICATION_FACTOR];
	String blockName;
	long offset;
	long size;
	
	DistFile parent;
	
	//0 unknown, 1 success, -1 failure  
	int[] successArray = new int[Constants.REPLICATION_FACTOR];
	
	Block(FileBlock fb, DistFile parent, long offset, long size){
		this.blockName = fb.getBlockFileName();
		this.parent = parent;
		this.offset = offset;
		this.size = size;
		
		for(int i=0; i<Constants.REPLICATION_FACTOR; i++){
			sendingEntities[i] = new SendingEntity(this, fb.getNodeLocations()[i]);
			successArray[i] = 0;
		}
	}
	
	void report(Boolean success, String nodeLocation){
		for(int i=0;i<Constants.REPLICATION_FACTOR;i++)
			if(sendingEntities[i].nodeLocation.equals(nodeLocation))
				successArray[i] = (success?1:-1);
		
		for(int b: successArray)
			if(b!=1)
				return;
		//TODO handle failure, i.e., b=-1 
		
		parent.report(blockName);
	}
};

class SendingEntity{
	Block parent;
	String nodeLocation;
	
	
	SendingEntity(Block parent, String nodeLocation){
		this.parent = parent;
		this.nodeLocation = nodeLocation;
	}
	
	void report(Boolean success){
		parent.report(success, nodeLocation);
	}
	

};