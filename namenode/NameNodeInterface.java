package namenode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import filesystem.FileBlock;
import filesystem.FileSystemException;

public interface NameNodeInterface extends Remote{
		// register does not return id for mapper since ID = ip:fileport:jobport
		public void register(String clientKey)throws RemoteException;
		
		public ArrayList<String> ls(String clientKey, String dirPath)throws RemoteException, FileSystemException, InvalidDataNodeException;
		
		public void mkdir(String clientKey, String newDirName)throws RemoteException, FileSystemException, InvalidDataNodeException;
		
		public void rm(String clientKey, String dirPath)throws RemoteException, FileSystemException, InvalidDataNodeException;
		
		// @return: a list of strings containing the blocks and 
		// 			the intended location of each block on various mappers 
		public FileBlock[] localToHDFS(String clientKey, String newDFSFileName, long fileSize)throws RemoteException, FileSystemException, InvalidDataNodeException;

		public void confirmLocalToHDFS(String clientKey, Boolean success, String fileName, FileBlock[] blocks)throws RemoteException, FileSystemException, InvalidDataNodeException;
		
		public void Heartbeat(String clientKey, long sizeOfStoredFiles, long freeSpace)throws RemoteException, InvalidDataNodeException;
		
		public void ConfirmDeletion(String clientKey, String blockName, String nodeLocation)throws RemoteException, InvalidDataNodeException;
		
		// TODO delete below and its implementation
		public String test() throws RemoteException;

		public FileBlock[] getFileBlocks(String clientKey, String HDFSFilePath) throws RemoteException, InvalidDataNodeException, FileSystemException;

		public ArrayList<String> getNewLocations(String clientKey, ArrayList<String> doneList,
				ArrayList<String> failList) throws RemoteException, InvalidDataNodeException, FileSystemException;;
}
