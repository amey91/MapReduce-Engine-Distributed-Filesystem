package datanode;

import java.rmi.RemoteException;

import namenode.InvalidDataNodeException;


//send a heartbeat containing info about datanode
public class HeartbeatThread extends Thread{
	public static boolean stopHB = false;
	@Override
	public void run(){
		while(true)
		{
			try {
				if(!stopHB)
					// TODO also send free processes at this datanode
					DataNode.nameNode.Heartbeat(DataNode.key,
							DataNode.getSizeOfFilesStored(), DataNode.getFreeSpace(), 
							DataNode.taskTrackerThread.getFreeProcesses(), 
							DataNode.taskTrackerThread.getTotalProcesses());

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
