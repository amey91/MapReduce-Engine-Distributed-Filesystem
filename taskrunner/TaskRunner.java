package taskrunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import commons.Logger;
import communication.Communicator;
import communication.Message;

public class TaskRunner {

	static int dataNodeListeningPort;
	public static void main(String[] args){
		if(args.length!=1){
			Logger.log("Invalid input!");
			return;
		}
		try {
			dataNodeListeningPort = Integer.parseInt(args[0]);
			
			ServerSocket listeningSocket = null;
			listeningSocket = new ServerSocket(0);
			int listeningPortNumber = listeningSocket.getLocalPort(); 
			
			Message m = new Message("TRConfirmation");
			m.portNumber = listeningPortNumber;
			Communicator.sendMessage("127.0.0.1", dataNodeListeningPort, m);

			Communicator.listenForMessages(listeningSocket, null, TaskRequestProcessor.class);
		} catch (InterruptedException|IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
}
