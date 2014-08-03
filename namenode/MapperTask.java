package namenode;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import commons.Logger;
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
	
	public int performOperation(){
		try {
			String slaveKey = NameNode.instance.findExecuteLocation(fileBlock.getNodeLocations());
			
			for(DataNodeInfo d: NameNode.instance.dataNodeList)
			if(d.getId().equals(slaveKey)){
				d.addRunningTask(this);
			}
			
			Logger.log("Mapper executing:"+taskId+" on "+slaveKey);
			Socket socket = Communicator.CreateTaskSocket(slaveKey);
			TaskMessage m = new TaskMessage("MapperTask", this);

			//block name actually
			m.fileName = fileBlock.getBlockFileName();
			Communicator.sendMessage(socket, m);		
			//for(Comparable<?> c:arr[iter])
				//Logger.log((String)c);
			return 0;
		} catch (IOException | InvalidDataNodeException e) {			
			e.printStackTrace();
			return -1;
		}
	}

	void execute() throws InvalidDataNodeException {
		
		NameNode.instance.taskQueue.addJob(this);
				
	}

}
