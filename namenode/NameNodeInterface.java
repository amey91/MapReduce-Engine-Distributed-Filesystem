package namenode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import filesystem.FileBlock;
import filesystem.FileSystemException;

public interface NameNodeInterface extends Remote{
		// register does not return id for mapper since ID = ip:fileport:jobport
		public void register(String myKey)throws RemoteException;
		
		public ArrayList<String> ls(String dirPath)throws RemoteException, FileSystemException;
		
		public void mkdir(String newDirName)throws RemoteException, FileSystemException;
		
		public void rm(String dirPath)throws RemoteException, FileSystemException;
		
		// @return: a list of strings containing the blocks and 
		// 			the intended location of each block on various mappers 
		public FileBlock[] localToHDFS(String clientKey, String newDFSFileName, long fileSize)throws RemoteException, FileSystemException;

		public void confirmLocalToHDFS(String clientKey, String fileName, FileBlock[] blocks, long[] blockSizes)throws RemoteException, FileSystemException;
		
		public void HDFSToLocal(String fileName)throws RemoteException; 
		
		public void Heartbeat(String clientKey, long sizeOfStoredFiles, long freeSpace)throws RemoteException;
		
		public void ConfirmDeletion(String blockName, String nodeLocation)throws RemoteException;
		
		// TODO delete below and its implementation
		public String test() throws RemoteException;
}
