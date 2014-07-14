package datanode;

import commons.Logger;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import namenode.NameNodeInterface;
import communication.Communicator;

public class DataNode {
	/*
	 * Listening port for incoming jobs
	 */
	static int fileListeningPort;
	static int jobListeningPort;
	static String myIp;
	static ServerSocket jobSocket;
	static ServerSocket fileSocket;
	static NameNodeInterface nameNode;
	static Path rootPath;
	static String key;
	public static long freeSpace=0;
	static Object freeSpaceLock = new Object();
	public static long sizeOfStoredFiles = 0;
	static Object sizeOfFileLock = new Object();
	
	public static void main(String args[]) {
		
		if(args.length!=3)
		{
			Logger.log("Usage: DataNode <RMIRegistry> <bindname> <rootPath>");
			// TODO 
			// return;
		}
		
		try {
			jobSocket = new ServerSocket(0);
			fileSocket = new ServerSocket(0);
			
			DataNode.fileListeningPort = fileSocket.getLocalPort();
			DataNode.jobListeningPort = jobSocket.getLocalPort();

			// TODO (#couldhave) read from config file
		     // nameNode = (NameNodeInterface) Naming.lookup("") //(args[0]+"/"+args[1]);
		     // nameNode.register(jobSocket.getInetAddress().getHostAddress(), DataNode.jobListeningPort,DataNode.fileListeningPort);
			Registry registry = LocateRegistry.getRegistry("127.0.0.1");
			
			nameNode = (NameNodeInterface) registry.lookup("RMI");
	       
	        DataNode.key = DataNode.generateKey(fileListeningPort,jobListeningPort);
	        Logger.log(nameNode.test());
	        nameNode.register(key);
	        
		    } catch (Exception e) {
		      System.out.println ("HelloClient exception: " + e);
		    }

		rootPath = Paths.get(args[0]);
		
		new Thread(new DataNodeConsoleThread()).start();
		new Thread(new HeartbeatThread()).start();
		new Thread(new TaskTracker()).start();
		new Thread(new FileSizeThread()).start();
		
		Communicator.listenForMessages(fileSocket, null, FileRequestProcessor.class);
		//TODO
		// Communicator.listenForMessages(jobSocket, null, JobRequestProcessor.class);
	}

	private static String generateKey(int fileListeningPort, int jobListeningPort) throws UnknownHostException{
		String a = InetAddress.getLocalHost().toString();
		a = a.substring(a.indexOf('/')+1);
		DataNode.myIp = a;
		return a+":"+fileListeningPort+":"+jobListeningPort;
	}
	
	
	public static void setFreeSpace(long a){
		synchronized(DataNode.freeSpaceLock){
			DataNode.freeSpace = a;
		}
	}
	
	public static long getFreeSpace(){
		synchronized(DataNode.freeSpaceLock){
			return DataNode.freeSpace;
		}
	}
	
	public static void setSizeOfFilesStored(long a){
		synchronized(DataNode.sizeOfFileLock){
			DataNode.sizeOfStoredFiles =a;
		}
	}
	
	public static long getSizeOfFilesStored(){
		synchronized(DataNode.sizeOfFileLock){
			return DataNode.sizeOfStoredFiles;
		}
	}
	
}
