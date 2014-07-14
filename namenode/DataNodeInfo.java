package namenode;

import java.util.ArrayList;
import java.util.Iterator;

import commons.Logger;

import filesystem.FileSystemException;

public class DataNodeInfo implements Comparable<DataNodeInfo>, Iterable<DataNodeInfo>{
	
	private String id;
	private long sizeOfStoredFiles;
	private long lastSeen;
	private long freeSpace;
	private ArrayList<String> fileProxyList;
	
	public DataNodeInfo(String id){
		this.setId(id);
		this.lastSeen = System.currentTimeMillis();
		fileProxyList = new ArrayList<String>();
	}
	
	/* getters and setters for this class */
	public String getId() {
		return id;
	}
	@Override 
	public boolean equals(Object other) {
		if(other instanceof DataNodeInfo)
			return false;
	    return id == ((DataNodeInfo)other).id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public long getsizeOfStoredFiles() {
		return sizeOfStoredFiles;
	}

	public void setsizeOfStoredFiles(long cumulativeSizeOfFiles) {
		this.sizeOfStoredFiles = cumulativeSizeOfFiles;
	}
	
	public void setLastSeen(long a){
		this.lastSeen = a;
	}
	
	public long getLastSeen(){
		return this.lastSeen;
	}
	
	public long getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}
	/* end of getters and setters for this class */
	
	@Override
	public int compareTo(DataNodeInfo in) {
		if(sizeOfStoredFiles < in.sizeOfStoredFiles)
			return -1;
		else if(sizeOfStoredFiles > in.sizeOfStoredFiles)
			return 1;
		return 0;
	}

	@Override
	public Iterator<DataNodeInfo> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addFileProxy(String newDFSFileName) {
		fileProxyList.add(newDFSFileName);
	}
	public void deleteFileProxy(String DFSFileName){
		fileProxyList.remove(DFSFileName);
	}

	public void shutDown() {

		// TODO Failure! Handle Files placed on this machine
		try
		{
			for(String proxy: fileProxyList)
				NameNode.fs.RemoveFileProxy(proxy);
		} catch(FileSystemException e){
			
			//TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
