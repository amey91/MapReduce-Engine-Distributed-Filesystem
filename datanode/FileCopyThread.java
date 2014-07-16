package datanode;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedQueue;

import namenode.InvalidDataNodeException;
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
					Thread.sleep(2000);
					if(!processSendingEntity(e)){
						e.report(false);
						throw new InterruptedException("failure: " + e.nodeLocation + " "  + e.parent.blockName);
					}
					else {
						Logger.log("success: " + e.nodeLocation + " "  + e.parent.blockName);
						additionQueue.remove(e);
						e.report(true);
					}
				}
				Thread.sleep(2000);

			} catch(InterruptedException ex){
				Logger.log("Error encountered while deleting distributed file: " + ex.getMessage() );
				//TODO delete
				ex.printStackTrace();
			}
		}
		
	}
	
	private Boolean processSendingEntity(SendingEntity e){
		//TODO 0  in separate thread?
		try
		{
			
			/*if(false && e.nodeLocation.equals(DataNode.key)){
				
				RandomAccessFile copyToLocal = new RandomAccessFile(e.parent.parent.localPath, "r");
				copyToLocal.seek(e.parent.offset);
				
				
				
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(e.parent.parent.localPath));
				
				bis.skip(e.parent.offset);
				
				File outFile = new File(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + e.parent.blockName));
				
				FileOutputStream fos = new FileOutputStream(outFile);
				if(!outFile.canWrite()){
					Logger.log("write error");
					bis.close();
					fos.close();
					return false;
				}
				
				long transferred = 0;
				byte[] byteArray = new byte[1024];
				
				while(transferred<e.parent.size){
					long left = (e.parent.size-transferred);
					int bytesToRead = byteArray.length;
					if(left < byteArray.length)
						bytesToRead = (int)left;
					
					int bytesRead = copyToLocal.read(byteArray, 0, bytesToRead);
					
					if(bytesRead<=0){
						break;
					}
					Logger.log("byteRead" + bytesRead);	
					fos.write(byteArray, 0 , bytesRead);
					transferred += bytesRead;
				}
				fos.close();
				copyToLocal.close();
				
				return actualSendSize == e.parent.size;
			}
			else{
					*/
			Socket socket [] = new Socket[1]; 
			
			String[] ipPort = AddressToIPPort.addressToIPPort(e.nodeLocation);
			
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
			Logger.log("Exception occured while sending file");
			//TODO delete
			ex.printStackTrace();
			return false;
		}
	}
}


class DistFile{
	
	Block[] blocks;
	String HDFSFilePath;
	String localPath;
	FileBlock[] fileBlocks;
	
	DistFile(String HDFSFilePath, FileBlock[] fileBlocks, String localPath, long[] sizes){
		this.HDFSFilePath = HDFSFilePath;
		this.localPath = localPath;
		blocks = new Block[fileBlocks.length];
		successArray = new Boolean[fileBlocks.length];
		this.fileBlocks = fileBlocks;
		
		long offset = 0;
		for(int i=0;i<fileBlocks.length;i++){
			blocks[i] = new Block(fileBlocks[i], this, offset, sizes[i]);
			offset += sizes[i];
			successArray[i] = false;
		}
	}

	Boolean[] successArray = new Boolean[Constants.REPLICATION_FACTOR];
	
	void report(String blockName){
		boolean complete = true;
		
		//check which block sent the confirmation
		for(int i=0;i<Constants.REPLICATION_FACTOR;i++)
			if(blocks[i].blockName.equals(blockName))
				successArray[i] = true;
		
		for(Boolean b: successArray)
			if(!b)//some confirmation not received
				complete = false;
		

		try {
			if(complete){
				//Got all confirmations, now send confirmation
				DataNode.nameNode.confirmLocalToHDFS(DataNode.key, HDFSFilePath, fileBlocks);
			}
		} catch (RemoteException | FileSystemException e) {
			// TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		} catch (InvalidDataNodeException e) {
			// TODO Auto-generated catch block
			DataNode.reset();
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
		
		//initialize succesArray
		for(int i=0; i<Constants.REPLICATION_FACTOR; i++){
			sendingEntities[i] = new SendingEntity(this, fb.getNodeLocations()[i]);
			successArray[i] = 0;
		}
	}
	
	void report(Boolean success, String nodeLocation){
		// update the node location for this file block which was sent successfully

		boolean complete = true;
		
		for(int i=0;i<Constants.REPLICATION_FACTOR;i++)
			if(sendingEntities[i].nodeLocation.equals(nodeLocation))
			{
				successArray[i] = (success?1:-1);
				break;
			}
		
		for(int b: successArray)
			if(b!=1)
				complete = false;
		//TODO handle failure, i.e., b=-1 
		
		//received all confirmations, send info to parent file
		if(complete)
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