package datanode;

import java.rmi.RemoteException;

import namenode.InvalidDataNodeException;

public class HeartbeatThread extends Thread{
	public static boolean stopHB = false;
	@Override
	public void run(){
		while(true)
		{
			try {
				if(!stopHB)
				DataNode.nameNode.Heartbeat(DataNode.key,
						DataNode.getSizeOfFilesStored(), DataNode.getFreeSpace());
				
				//TODO check this sleep value
				Thread.sleep(2000);
				
			} catch (RemoteException | InterruptedException e) {
				// TODO delete
				e.printStackTrace();
			} catch (InvalidDataNodeException e) {
				DataNode.reset();
			}
		}
	}
}
