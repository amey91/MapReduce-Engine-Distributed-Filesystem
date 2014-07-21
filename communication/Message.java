package communication;

import java.io.Serializable;


//type of message.
// numerous other message types are derived from it
public class Message implements Serializable{
	//message can be of type = "add" | "remove" 
	//						 = "start" | "stop"	
	private static final long serialVersionUID = 6215667127323595530L;

	public String type;

	public String fileName;
	
	public String sendLocation;
	
	public long fileSize;
	
	public int portNumber;
	
	public Message(String type) {
		this.type = type;
	}
	
	public Message(String type, String fileName) {
		this.type = type;
		this.fileName = fileName;
	}
	
	public String toString() {
		return type;
	}
}

