package datanode;

import java.io.IOException;
import java.net.Socket;

import communication.Communicator;
import communication.Message;

public class FileRequestProcessor extends Thread{
	Socket socket;
	
	public FileRequestProcessor(Object rorTable, Socket a){
		this.socket = a; 
	}
	
	@Override
	public void run(){
		try{
			Message m = Communicator.receiveMessage(socket);
			if(m.toString() == "add"){
				//TODO
			}
			else if(m.toString() == "remove" ){
				//TODO
			}
		}
		catch(IOException | ClassNotFoundException | InterruptedException e){
			
		}
		
	}
}
