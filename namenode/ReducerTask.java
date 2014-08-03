package namenode;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import commons.Logger;
import communication.Communicator;
import communication.TaskMessage;


public class ReducerTask extends Task implements Serializable{

	private static final long serialVersionUID = -4477167297113637571L;

	int numMappers;
	String[] clients;
	ReducerTask(JobTracker parent, int taskId, Comparable<?> startKey, Comparable<?> endKey, int numMappers) {
		super(parent, taskId);

		clients = new String[numMappers];
	}

	public String[] getClients(){
		return clients;
	}

	public int performOperation(){
		try {
			
			String slaveKey = NameNode.instance.findExecuteLocation(clients);

			for(DataNodeInfo d: NameNode.instance.dataNodeList)
				if(d.getId().equals(slaveKey)){
					d.addRunningTask(this);
				}

			Logger.log("Reducer executing:"+taskId+" on "+slaveKey);
			
			Socket socket = Communicator.CreateTaskSocket(slaveKey);
			TaskMessage m = new TaskMessage("ReducerTask", this);

			Communicator.sendMessage(socket, m);
			return 0;
		} catch (IOException | InvalidDataNodeException e) {
			e.printStackTrace();
			return -1;
		}
	}

	void execute() throws InvalidDataNodeException {
		NameNode.instance.taskQueue.addJob(this);
	}


	public void addProvider(int mapperTaskId, String clientKey) {
		clients[mapperTaskId] = clientKey;
	}


	public String getReducerName() {
		// TODO Auto-generated method stub
		return parent.reducerClassName;
	}

}
