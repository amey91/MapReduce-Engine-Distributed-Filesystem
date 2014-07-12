package datanode;

import java.io.IOException;
import java.net.Socket;

import communication.Communicator;
import communication.Message;

public class JobRequestProcessor extends Thread{
	Socket socket;
	
	public JobRequestProcessor(Object rorTable, Socket a){
		this.socket = a; 
	}
	
	@Override
	public void run(){
		try{
			Message m = Communicator.receiveMessage(socket);
			if(m.toString() == "run_job"){
				//TODO
			}
			else if(m.toString() == "stop_job" ){
				//TODO
			}
		}
		catch(IOException | ClassNotFoundException | InterruptedException e){
			
		}
		
	}
}