package datanode;

import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	static NameNodeInterface nameNode;
	static Path rootPath;
	
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

		rootPath = Paths.get(args[2]);
		
		new Thread(new ConsoleThread()).start();
		new Thread(new HeartbeatThread()).start();
		new Thread(new TaskTracker()).start();
		
		Communicator.listenForMessages(fileSocket, null, FileRequestProcessor.class);
		//TODO
		// Communicator.listenForMessages(jobSocket, null, JobRequestProcessor.class);
	}
	
	
	
}
