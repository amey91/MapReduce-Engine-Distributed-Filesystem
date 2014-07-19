package datanode;

import jarmanager.JarLoader;

import java.io.IOException;
import java.net.Socket;

import mapreduce.Mapper;
import namenode.InitTask;
import commons.Logger;
import communication.Communicator;
import communication.KeyListMessage;
import communication.Message;
import communication.TaskMessage;
import filesystem.FileSystem;

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
			if(m.type.equals("InitTask")){
				
				// run sample mapper task
				// calculate total size estimate
				// return keys
				
				TaskMessage tm = (TaskMessage) m;
				InitTask t = (InitTask) tm.task;
				String blockLocalPath = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + tm.fileName);
				
				
				String jarFileLocalPath = DataNode.rootPath.toString()+FileSystem.DIRECTORYSEPARATOR + t.getJob().getID() + ".jar";
				HDFSToLocal.MoveToLocal(jarFileLocalPath, t.getJarFile());
				
				KeyListMessage klm =  DataNode.getTaskRunnerManager().LaunchInitTask( jarFileLocalPath, t.getMapperName(), blockLocalPath);
				
				Communicator.sendMessage(socket, klm);
				
				socket.close();
			}
			else if(m.type.equals("Mapper")){
			}
		}
		catch(IOException | ClassNotFoundException | InterruptedException e){
			
		}
		
	}
}