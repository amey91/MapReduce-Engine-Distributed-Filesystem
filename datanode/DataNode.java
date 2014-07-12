package datanode;

import java.net.ServerSocket;
import java.rmi.Naming;

import namenode.NameNodeInterface;
import communication.Communicator;

public class DataNode {
	/*
	 * Listening port for incoming jobs
	 */
	static int fileListeningPort;
	static int jobListeningPort;
	static ServerSocket jobSocket;
	static ServerSocket fileSocket;
	public static NameNodeInterface nameNode;
	
	public static void main(String args[]){
		try {
			jobSocket = new ServerSocket();
			fileSocket = new ServerSocket();
			DataNode.fileListeningPort = fileSocket.getLocalPort();
			DataNode.jobListeningPort = jobSocket.getLocalPort();
			
			// TODO (#couldhave) read from config file
		      nameNode = (NameNodeInterface) Naming.lookup (args[0]+"/"+args[1]);
		      nameNode.register(jobSocket.getInetAddress().getHostAddress(), DataNode.jobListeningPort,DataNode.fileListeningPort);
		    } catch (Exception e) {
		      System.out.println ("HelloClient exception: " + e);
		    }
		
		new Thread(new Console()).start();
		
		Communicator.listenForMessages(fileSocket, null, FileRequestProcessor.class);
		//TODO
		// Communicator.listenForMessages(jobSocket, null, FileRequestProcessor.class);
	}
	
	
	
}
