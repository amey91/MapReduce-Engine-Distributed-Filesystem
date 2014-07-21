package namenode;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import communication.Communicator;
import communication.TaskMessage;

import filesystem.FileBlock;

// one type of task. See Task.java
public class MapperTask extends Task implements Serializable{

	private static final long serialVersionUID = -8439626879678851485L;
	Comparable<?>[] splits;
	FileBlock fileBlock;
	MapperTask(JobTracker jobTracker, int taskId, FileBlock fb) {
		super(jobTracker, taskId);
		this.fileBlock = fb;
	}
	

	void setSplits(Comparable<?>[] splits){
		this.splits = splits;
	}
	public String getMapperName(){
		return parent.mapperClassName;
	}
	public Comparable<?>[] getSplits(){
		return splits;
	}

	void execute() throws InvalidDataNodeException {
		
		try {
			
			String slaveKey = NameNode.instance.findExecuteLocation(fileBlock.getNodeLocations());
			
			Socket socket = Communicator.CreateTaskSocket(slaveKey);
			TaskMessage m = new TaskMessage("MapperTask", this);

			//block name actually
			m.fileName = fileBlock.getBlockFileName();
			Communicator.sendMessage(socket, m);			
			//for(Comparable<?> c:arr[iter])
				//Logger.log((String)c);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
