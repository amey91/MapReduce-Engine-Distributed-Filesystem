package datanode;

import java.io.IOException;
import java.net.Socket;

import mapreduce.Mapper;
import namenode.InitTask;
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
			if(m.type.equals("InitJob")){
				TaskMessage tm = (TaskMessage) m;
				InitTask t = (InitTask) tm.task;
				String blockName = tm.fileName;
				
				String jarFileLocalPath = DataNode.rootPath.toString()+FileSystem.DIRECTORYSEPARATOR + t.getJob().getID() + ".jar";
				HDFSToLocal.MoveToLocal(jarFileLocalPath, t.getJarFile());
				Class<Mapper> mapper = getClassFromJar(jarFileLocalPath, t.getMapperName());
				
				Boolean initTask = true;
				KeyListMessage klm = runInitMapper(mapper, blockName, initTask);
				
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