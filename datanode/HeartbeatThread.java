package datanode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class HeartbeatThread extends Thread{
	
	@Override
	public void run(){
		while(true)
		{
			try {
				DataNode.nameNode.Heartbeat(InetAddress.getLocalHost().getHostAddress(), DataNode.jobListeningPort);

				//TODO check this sleep value
				Thread.sleep(1000);
				
			} catch (RemoteException | UnknownHostException | InterruptedException e) {
				// TODO delete
				e.printStackTrace();
			}
		}
	}
}
