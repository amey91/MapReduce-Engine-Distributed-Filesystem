package namenode;

import java.rmi.RemoteException;
import java.rmi.Naming;

public class NameNode extends Thread implements NameNodeInterface {
	// register does not return id for mapper since ID = ip, port
	public void register(String myIP, int listeningPort)throws RemoteException{
		
	}
	public String[] ls(String dirPath)throws RemoteException{
		// TODO change this
		return null;
	}
	public int mkdir(String newDirName)throws RemoteException{
		// TODO change this
		return -1;
	}
	public int rm(String dirPath)throws RemoteException{
		// TODO change this
		return -1;
	}
	// @return: a list of strings containing the blocks and 
	// 			the intended location of each block on various mappers 
	public String[] localToHDFS(String newDFSFileName, long fileSize)throws RemoteException{
		// TODO change this
		return null;
	}
	public void confirmLocalToHDFS(String fileName, long[] blockSizes)throws RemoteException{
		
	}
	// TODO 
	public void HDFSToLocal(String fileName)throws RemoteException{
		
	}
	@Override
	public void register(String myIP, int jobListeningPort,
			int fileListeningPort) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void Heartbeat(String hostname, int port) throws RemoteException {
		// TODO Auto-generated method stub		
	}
	
}
