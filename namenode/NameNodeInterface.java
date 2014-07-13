package namenode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import filesystem.FileBlock;
import filesystem.FileSystemException;

public interface NameNodeInterface extends Remote{
		// register does not return id for mapper since ID = ip:fileport:jobport
		public void register(String myKey)throws RemoteException;
		
		public ArrayList<String> ls(String dirPath)throws RemoteException;
		
		public void mkdir(String newDirName)throws RemoteException, FileSystemException;
		
		public int rm(String dirPath)throws RemoteException;
		
		// @return: a list of strings containing the blocks and 
		// 			the intended location of each block on various mappers 
		public FileBlock[] localToHDFS(String newDFSFileName, long fileSize)throws RemoteException;

		public void confirmLocalToHDFS(String fileName, FileBlock[] blocks, long[] blockSizes)throws RemoteException, FileSystemException;
		
		public void HDFSToLocal(String fileName)throws RemoteException; 
		
		public void Heartbeat(String hostname, int filePort, int dataPort)throws RemoteException;
		
		// TODO delete below and its implementation
		public String test() throws RemoteException;
}
