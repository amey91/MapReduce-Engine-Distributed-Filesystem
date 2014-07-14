package datanode;

import java.rmi.RemoteException;

public class HeartbeatThread extends Thread{
	
	@Override
	public void run(){
		while(true)
		{
			try {
				DataNode.nameNode.Heartbeat(DataNode.key,
						DataNode.getSizeOfFilesStored(), DataNode.getFreeSpace());
				
				//TODO check this sleep value
				Thread.sleep(2000);
				
			} catch (RemoteException | InterruptedException e) {
				// TODO delete
				e.printStackTrace();
			}
		}
	}
}
