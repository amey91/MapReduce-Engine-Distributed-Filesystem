package namenode;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameNodeInterface extends Remote{
	// register does not return id for mapper since ID = ip, port
		public void register(String myIP, int jobListeningPort, int fileListeningPort)throws RemoteException;
		public String[] ls(String dirPath)throws RemoteException;
		public int mkdir(String newDirName)throws RemoteException;
		public int rm(String dirPath)throws RemoteException;
		// @return: a list of strings containing the blocks and 
		// 			the intended location of each block on various mappers 
		public String[] localToHDFS(String newDFSFileName, long fileSize)throws RemoteException;
		public void confirmLocalToHDFS(String fileName, long[] blockSizes)throws RemoteException; 
		public void HDFSToLocal(String fileName)throws RemoteException;
}
