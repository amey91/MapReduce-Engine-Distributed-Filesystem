package namenode;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

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

	void execute() throws InvalidDataNodeException {
		try {
			
			String slaveKey = NameNode.instance.findExecuteLocation(clients);
			
			Socket socket = Communicator.CreateTaskSocket(slaveKey);
			TaskMessage m = new TaskMessage("ReducerTask", this);

			Communicator.sendMessage(socket, m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void addProvider(int mapperTaskId, String clientKey) {
		clients[mapperTaskId] = clientKey;
	}


	public String getReducerName() {
		// TODO Auto-generated method stub
		return parent.reducerClassName;
	}

}
