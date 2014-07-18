package datanode;

import commons.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import namenode.NameNodeInterface;
import communication.Communicator;
import communication.Message;

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
	public static Path rootPath;
	static String key;
	public static long freeSpace=0;
	static Object freeSpaceLock = new Object();
	public static long sizeOfStoredFiles = 0;
	static Object sizeOfFileLock = new Object();

	static FileCopyThread fcThread;
	static DataNodeConsoleThread consoleThread;
	static HeartbeatThread hbThread;
	static FileSizeThread fsThread;
	static TaskTracker taskTrackerThread;
	
	public static void main(String args[]) {
		
		if(args.length!=3)
		{
			// TODO Logger.log("Usage: DataNode <RMIRegistry> <bindname> <rootPath>");
			Logger.log("Usage: DataNode <rootPath>");
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
	       // Logger.log(nameNode.test());
	        nameNode.register(key);
	        
		    } catch (Exception e) {
		      System.out.println ("HelloClient exception: " + e);
		      System.exit(1);
		    }

		rootPath = Paths.get(args[0]);
		
		consoleThread = new DataNodeConsoleThread();
		consoleThread.start();
		hbThread = new HeartbeatThread();
		hbThread.start();
		taskTrackerThread = new TaskTracker();
		taskTrackerThread.start();
		fsThread = new FileSizeThread();
		fsThread.start();
		fcThread = new FileCopyThread();
		fcThread.start();
		
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

	public static void reset() {
		//required to make sure that the thread is not killing itself
		Logger.log("reset1");
		Message m = new Message("reset");
		try {
			Socket socket = Communicator.CreateDataSocket(DataNode.key);
			Communicator.sendMessage(socket, m);
			socket.close();
			Thread.sleep(1000000);
		} catch (InterruptedException|IOException e){
			//TODO delete
			Logger.log(e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	public static void resetAllThreads() {
		Logger.log("reset2");
		
		// will never called from the threads
		fcThread.stop();
		consoleThread.stop();
		hbThread.stop();
		fsThread.stop();
		taskTrackerThread.stop();
		File folder = new File(DataNode.rootPath.toString());
		File[] listOfFiles = folder.listFiles();
		
		if(listOfFiles==null){
			Logger.log("FILE DOES NOT EXIST OR WRONG PATH");
			System.exit(0);
		}
		DataNode.setFreeSpace(folder.getFreeSpace());
		for (int i = 0; i < listOfFiles.length; i++) {
			listOfFiles[i].delete();
		}
		
		consoleThread = new DataNodeConsoleThread();
		consoleThread.start();
		hbThread = new HeartbeatThread();
		hbThread.start();
		taskTrackerThread = new TaskTracker();
		taskTrackerThread.start();
		fsThread = new FileSizeThread();
		fsThread.start();
		fcThread = new FileCopyThread();
		fcThread.start();
		
        try {
			nameNode.register(key);
		} catch (RemoteException e) {
			// TODO try again?
			Logger.log("Couldn't re register myself");
			System.exit(0);
		}
	}	
}
