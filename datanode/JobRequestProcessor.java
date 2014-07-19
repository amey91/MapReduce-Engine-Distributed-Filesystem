package datanode;


import java.io.IOException;
import java.net.Socket;

import commons.Logger;
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
			Logger.log("Received Message: " + m.type);
			
			if(m.type.equals("Mapper")){
			}
		}
		catch(IOException | ClassNotFoundException | InterruptedException e){
			e.printStackTrace();
		}
		
	}
}