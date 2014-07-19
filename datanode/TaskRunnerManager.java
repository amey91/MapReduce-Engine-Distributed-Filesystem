package datanode;
import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Arrays;

import mapreduce.Mapper;
import commons.Logger;
import communication.Communicator;
import communication.KeyListMessage;
import communication.MapperTaskMessage;

/*  @referred: http://www.xyzws.com/Javafaq/how-to-run-external-programs-by-using-java-processbuilder-class/189
 *  Launches a separate JVM with the specified path
 *  @param0: localRootPath
 *  @param1: className inside any local folder
 *  @param2: outputFile inside local root folder
 *  @param3: (OPTIONAL) additional arguments from Application Programmer for the mapper
 *  
 *  e.g. java LaunchProcess 
 */
public class TaskRunnerManager extends Thread { 
	
	String jarFileLocalPath;
	String mapperClassName;
	String blockName;
	Process runningProcess;
	ServerSocket listeningSocket;
	int listeningPort;
	String rootPath;
	int taskRunnerPort;
	Boolean isReady = false;
	Boolean isRunning = false;
	Thread listeningThread;
	long lastHeartBeat;
	double percentCompletion;
	
	public TaskRunnerManager(String rootPath) throws IOException{
		this.listeningSocket = new ServerSocket(0);
		this.listeningPort = this.listeningSocket.getLocalPort();
		this.rootPath = rootPath;
	}
	
	private void CreateNewJVM(){
		
		if(runningProcess!=null)
			runningProcess.destroy();
		try{
			//String[] command = {"java.exe","-cp", "C:/Users/Amey/workspace/example3", "taskrunner.TaskRunner", String.valueOf(this.listeningPort)};
			String[] command = {"java.exe","-cp", "E:/example/example3", "taskrunner.TaskRunner", String.valueOf(this.listeningPort)};
			// String[] command = {"java.exe","-cp", "./", "jobhandler.StartJob","testJobName","testrootpath","testclassName","args"};
			ProcessBuilder probuilder = new ProcessBuilder( command );
			//probuilder.directory(new File("c:/Temp"));
			probuilder.directory(new File(rootPath));
			Process process = probuilder.start();
			this.runningProcess = process;
			
			System.out.printf("Output of running %s is:\n",
					Arrays.toString(command));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		Logger.log("creating new jvm");
			CreateNewJVM();
			Communicator.listenForMessages(listeningSocket, this, TaskRunnerListeningThread.class);
	}

	public Boolean isReady(){
		return isReady;
	}
	
	public Boolean isRunning(){
		return isRunning;
	}
	

	public KeyListMessage LaunchInitTask(String jarFileLocalPath, String mapperClassName, String blockLocalPath) throws UnknownHostException, InterruptedException, IOException, ClassNotFoundException{
		
		if(!isReady)
			throw new IOException("TaskRunner not ready yet");

		lastHeartBeat = System.currentTimeMillis();
		isRunning = true;
		
		MapperTaskMessage mtm = new MapperTaskMessage("InitTask", jarFileLocalPath, mapperClassName, blockLocalPath, null);
		isRunning = false;
		
		return (KeyListMessage)Communicator.sendAndReceiveMessage("127.0.0.1", taskRunnerPort, mtm);
	}
	
	public void LaunchMapperTask(String jarFileLocalPath, String mapperClassName, String blockLocalPath, String outputPath ) throws UnknownHostException, InterruptedException, IOException{
		
		if(!isReady)
			throw new IOException("TaskRunner not ready yet");

		lastHeartBeat = System.currentTimeMillis();
		isRunning = true;
		
		MapperTaskMessage mtm = new MapperTaskMessage("MapperTask", jarFileLocalPath, mapperClassName, blockLocalPath, outputPath);
		Communicator.sendMessage("127.0.0.1", taskRunnerPort, mtm);
		
	}
	
	public void HeartbeatFailed(){
		CreateNewJVM();
		// TODO update map of tasks
	}
	
	public void MarkComplete(){
		isRunning = false;
		// TODO update map of tasks
	}
	public void setTaskRunnerPort(int taskRunnerPort) {
		isReady= true;
		this.taskRunnerPort = taskRunnerPort;
		
	}

	public void sendUpdate(Boolean complete, double percent) {
		lastHeartBeat = System.currentTimeMillis();
		if(complete)
			MarkComplete();
		percentCompletion = percent;
	}
}
