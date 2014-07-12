package datanode;

import java.rmi.RemoteException;

public class LocalToHDFS extends Thread {
	String localFilePath;
	String HDFSFilePath;
	
	public LocalToHDFS(String localFilePath, String hDFSFilePath) {
		this.localFilePath = localFilePath;
		this.HDFSFilePath = hDFSFilePath;
	}
	@Override
	public void run(){
		// referred to http://stackoverflow.com/questions/2149785/get-size-of-folder-or-file
		java.io.File file = new java.io.File(localFilePath);
		try {
			DataNode.nameNode.localToHDFS(HDFSFilePath, file.length());
			
		} catch (RemoteException e) {
			// TODO delete 
			e.printStackTrace();
		}
		
	}
	
}
