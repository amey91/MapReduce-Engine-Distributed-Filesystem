package datanode;

import java.io.IOException;
import java.net.Socket;

import commons.Logger;
import communication.Communicator;
import communication.HeartbeatMessage;
import communication.Message;

public class TaskRunnerListeningThread extends Thread {

	Socket socket;
	TaskRunnerManager taskRunnerManager;
	public TaskRunnerListeningThread(Object taskRunnerManager, Socket a){
		this.socket = a;
		this.taskRunnerManager = (TaskRunnerManager) taskRunnerManager;
	}

	@Override
	public void run(){
		Message inMessage;
		try {
			inMessage = Communicator.receiveMessage(socket);Logger.log("received message: " + inMessage.type);
			
			if(inMessage.type.equals("TRConfirmation")){
				
				taskRunnerManager.setTaskRunnerPort(inMessage.portNumber);
				
			}
			else if(inMessage.type.equals("Heartbeat")){
				
				HeartbeatMessage hbm = (HeartbeatMessage) inMessage;
				taskRunnerManager.sendUpdate(hbm.complete, hbm.percent);
				
			}
		} catch (ClassNotFoundException | InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
