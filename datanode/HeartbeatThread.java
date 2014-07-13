package datanode;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class HeartbeatThread extends Thread{
	
	@Override
	public void run(){
		while(true)
		{
			try {
				DataNode.nameNode.Heartbeat(Inet4Address.getLocalHost().getHostAddress(), 
						DataNode.fileListeningPort, DataNode.jobListeningPort);
				
				//TODO check this sleep value
				Thread.sleep(2000);
				
			} catch (RemoteException | UnknownHostException | InterruptedException e) {
				// TODO delete
				e.printStackTrace();
			}
		}
	}
}
