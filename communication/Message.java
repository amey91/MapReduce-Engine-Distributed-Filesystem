package communication;

import java.io.Serializable;

public class Message implements Serializable{
	//message can be of type = "add" | "remove" 
	//						 = "start" | "stop"	
	private static final long serialVersionUID = 6215667127323595530L;

	public String type; 
	
	public long jobId;
	
	public String fileName;
	
	public long fileSize;
	
	public Message(String type) {
		this.type = type;
	}
	
	public Message(long jobId, String type, String fileName) {
		this.jobId = jobId;
		this.type = type;
		this.fileName = fileName;
	}
	
	public String toString() {
		return type;
	}
}

