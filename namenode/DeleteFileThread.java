package namenode;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import commons.AddressToIPPort;
import commons.Logger;
import communication.Communicator;
import communication.Message;


public class DeleteFileThread extends Thread{

	ConcurrentLinkedQueue<BlockLocationPair> deletionQueue 
						= new ConcurrentLinkedQueue<BlockLocationPair>();

	
	public void push(String fileName, String nodeLocation){
		deletionQueue.add(new BlockLocationPair(fileName, nodeLocation));
	}

	public void remove(String fileName, String nodeLocation){
		deletionQueue.remove(new BlockLocationPair(fileName, nodeLocation));
	}
	
	@Override 
	public void run(){
		
		while(true){
			try{
				for(BlockLocationPair p : deletionQueue){
					String nodeLocation = p.nodeLocation;
					String blockName = p.blockName;
					String[] ipPort;
					try {
						ipPort = AddressToIPPort.addressToIPPort(nodeLocation);
						Message inputMessage = new Message("remove");
						inputMessage.fileName = blockName;
						
						Communicator.sendMessage(ipPort[0], Integer.parseInt(ipPort[1]), inputMessage);
					} catch (NumberFormatException | InterruptedException | IOException e) {
						// TODO delete
						e.printStackTrace();
						throw new IOException("Could not delete distributed file block " + blockName +"\n "+e.getMessage());
					}	
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
}
