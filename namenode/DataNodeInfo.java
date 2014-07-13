package namenode;

import java.util.Calendar;
import java.util.Iterator;

public class DataNodeInfo implements Comparable<DataNodeInfo>, Iterable<DataNodeInfo>{
	
	private String id;
	private long cumulativeSizeOfFiles;
	private long lastSeen;
	
	public DataNodeInfo(String id){
		this.setId(id);
		this.lastSeen = System.currentTimeMillis();
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

	public long getCumulativeSizeOfFiles() {
		return cumulativeSizeOfFiles;
	}

	public void setCumulativeSizeOfFiles(long cumulativeSizeOfFiles) {
		this.cumulativeSizeOfFiles = cumulativeSizeOfFiles;
	}
	
	public void setLastSeen(long a){
		this.lastSeen = a;
	}
	
	public long getLastSeen(){
		return this.lastSeen;
	}
	/* end of getters and setters for this class */
	
	@Override
	public int compareTo(DataNodeInfo in) {
		if(cumulativeSizeOfFiles < in.cumulativeSizeOfFiles)
			return -1;
		else if(cumulativeSizeOfFiles > in.cumulativeSizeOfFiles)
			return 1;
		return 0;
	}

	@Override
	public Iterator<DataNodeInfo> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
