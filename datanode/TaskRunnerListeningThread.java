package datanode;

import java.io.IOException;
import java.net.Socket;

import commons.Logger;
import communication.Communicator;
import communication.HeartbeatMessage;
import communication.Message;


// Listens to a particular task runner process on the datanode
public class TaskRunnerListeningThread extends Thread {

	Socket socket;
	TaskRunnerManager taskRunnerManager;
	public TaskRunnerListeningThread(Object taskRunnerManager, Socket a){
		this.socket = a;
		this.taskRunnerManager =  (TaskRunnerManager)taskRunnerManager;
	}

	@Override
	public void run(){
		try {

			Message inMessage = Communicator.receiveMessage(socket);Logger.log("received message: " + inMessage.type);

			Logger.log("Received message from taskRunner: " + inMessage.type);
			
			if(inMessage.type.equals("TRConfirmation")){
				
				taskRunnerManager.setTaskRunnerPort(inMessage.portNumber);
				
			}
			else if(inMessage.type.equals("Heartbeat")){
				
				HeartbeatMessage hbm = (HeartbeatMessage) inMessage;
				taskRunnerManager.sendUpdate( hbm.percent,hbm.complete);
				
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
