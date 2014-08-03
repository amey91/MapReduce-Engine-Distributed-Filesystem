package namenode;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import commons.Logger;
import filesystem.FileBlock;
import filesystem.FileSystemException;


// info for all datanodes
// essential in scheduling jobs/files
public class DataNodeInfo implements Comparable<DataNodeInfo>{
	
	private String id;
	private long sizeOfStoredFiles;
	private long lastSeen;
	private long freeSpace;
	private int freeProcesses;
	private int totalProcesses;
	private ArrayList<String> fileProxyList;
	// these file blocks will temporarily store the unconfirmed blocks for each file
	public ArrayList<FileBlock> tempFileBlocks;
	
	public ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();
	
	public DataNodeInfo(String id){
		this.setId(id);
		this.lastSeen = System.currentTimeMillis();
		fileProxyList = new ArrayList<String>();
		tempFileBlocks = new ArrayList<FileBlock>();
	}
	
	/* getters and setters for this class */
	public String getId() {
		return id;
	}
	
	public void setFileBLocks(FileBlock[] f){
		for(FileBlock fb : f)
		tempFileBlocks.add(fb);
	}
	
	
	public void deleteTempFileBlock(FileBlock[] toBeDeleted){
		Logger.log("TEMP BEFORE deletion: " + tempFileBlocks.size());
		for(FileBlock temp : toBeDeleted)
			tempFileBlocks.remove(temp);	
		tempFileBlocks.remove(toBeDeleted);
		Logger.log("TEMP AFTER deletion: " + tempFileBlocks.size());
	}
	
	@Override 
	public boolean equals(Object other) {
		if(other instanceof DataNodeInfo)
			return id.equals( ((DataNodeInfo)other).id);
		else if(other instanceof String)
			return id.equals( (String)other);
		else
			return false;
				
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


	public void addFileProxy(String newDFSFileName) {
		fileProxyList.add(newDFSFileName);
	}
	public void deleteFileProxy(String DFSFileName){
		fileProxyList.remove(DFSFileName);
	}

	public void shutDown() {

		// Failure! Handle Files placed on this machine
		try
		{
			//delete any temporary file name blocked by this data node
			for(String proxy: fileProxyList)
				NameNode.fs.RemoveFileProxy(proxy);
			//TODO put all blocks of this node into deletequeue
			
			//for all fileblocks on that client
			for(FileBlock fb : tempFileBlocks){
				for(String deleteLocation:fb.getNodeLocations()){
					if(!id.equals(deleteLocation))
						NameNode.instance.deleteThread.push(fb.getBlockFileName(), deleteLocation);
				}
			}//end of push to delete thread	
			
			for(Task t:taskQueue)
				NameNode.instance.taskQueue.addJob(t);
			
		} catch(FileSystemException e){
			
			//TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		}
	}

	public int getFreeProcesses() {
		return freeProcesses;
	}

	public void setFreeProcesses(int freeProcesses) {
		this.freeProcesses = freeProcesses;
	}

	public int getTotalProcesses() {
		return totalProcesses;
	}

	public void setTotalProcesses(int totalProcesses) {
		this.totalProcesses = totalProcesses;
	}

	public void addRunningTask(Task task) {
		taskQueue.add(task);
		
	}
	
	public void removeRunningTask(Task task) {
		for(Task t:taskQueue){
			if(t.getJob().getID() == task.getJob().getID() && t.getTaskID() == task.getTaskID()){
				if(t instanceof MapperTask &&task instanceof MapperTask)
					taskQueue.remove(t);
				if(t instanceof ReducerTask &&task instanceof ReducerTask)
					taskQueue.remove(t);
			}
		}
	}
	
}
