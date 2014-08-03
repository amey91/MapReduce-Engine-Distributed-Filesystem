package datanode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import namenode.InitTask;
import namenode.InvalidDataNodeException;
import namenode.MapperTask;
import namenode.ReducerTask;
import commons.FileMerge;
import commons.Logger;
import communication.Communicator;
import communication.HeartbeatMessage;
import communication.KeyListMessage;
import communication.MergeAndUploadMessage;
import communication.Message;
import communication.TaskMessage;
import filesystem.FileSystem;


// this thread is responsible for processing requests received on 
// the file port of the datande
public class FileRequestProcessor extends Thread{
	Socket socket;

	public FileRequestProcessor(Object unused, Socket a){
		this.socket = a; 
	}

	@Override
	public void run(){
		try{
			Message inMessage = Communicator.receiveMessage(socket);
			
			if(inMessage.type.equals("add")){
				
				Communicator.receiveFile(socket, 
						DataNode.rootPath + (filesystem.FileSystem.DIRECTORYSEPARATOR + inMessage.fileName),
						inMessage.fileSize);
			    socket.close();
			}
			else if(inMessage.type.equals("remove")){
				String blockName = inMessage.fileName;
				String blockPath = DataNode.rootPath+"/"+blockName;
				File fileTemp = new File(blockPath);
				if (fileTemp.exists()){
				    Logger.log("From delete File: " + inMessage.fileName);
					fileTemp.delete();
					DataNode.nameNode.ConfirmDeletion(DataNode.key, blockName, DataNode.key);
				}
				else
					DataNode.nameNode.ConfirmDeletion(DataNode.key, blockName, DataNode.key);
				socket.close();
			}
			else if(inMessage.type.equals("reset")){
				DataNode.resetAllThreads();
			}
			else if(inMessage.type.equals("sendMeFile")){
				String blockName = inMessage.fileName;
				File sendFile = new File(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName));

				inMessage.fileSize = sendFile.length();
				Communicator.sendMessage(socket, inMessage);
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName)));
				Communicator.sendStream(socket, bis, sendFile.length());
			}
			else if(inMessage.type.equals("sendFile")){
				
				try{
					String blockName = inMessage.fileName;
					Socket outSocket = Communicator.CreateDataSocket(inMessage.sendLocation);
					
					File sendFile = new File(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName));
					BufferedInputStream bis = new BufferedInputStream(
							new FileInputStream(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName)));
					
					
					// add a new file to datanode
					Message m = new Message("add");
					m.fileName = blockName;
					m.fileSize = sendFile.length();
					Communicator.sendMessage(outSocket, m);
					
					Boolean success = Communicator.sendStream(outSocket, bis, sendFile.length())==sendFile.length();
					
					Message confirmation = new Message(success?"success":"fail");
					
					Communicator.sendMessage(socket, confirmation);
					
					outSocket.close();
					socket.close();
					
				}catch(IOException e){
					try {
						
						Logger.log(e.getMessage());
						e.printStackTrace();
						
						Message confirmation = new Message("fail");
						Communicator.sendMessage(socket, confirmation);
						socket.close();
						
					} catch (IOException e1) {
						Logger.log("Oh well!");
						e1.printStackTrace();
					}
				}
			} else if(inMessage.type.equals("InitTask")){
				
				// run sample mapper task
				// calculate total size estimate
				// return keys
				
				TaskMessage tm = (TaskMessage) inMessage;
				InitTask t = (InitTask) tm.task;
				String blockLocalPath = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + tm.fileName);
				
				
				String jarFileLocalPath = DataNode.rootPath.toString()+FileSystem.DIRECTORYSEPARATOR + t.getJob().getID() + ".jar";
				HDFSToLocal.MoveToLocal(jarFileLocalPath, t.getJarFilePath());
				
				TaskRunnerManager trm = DataNode.taskTrackerThread.getTaskRunnerManager(true);
				if(trm==null){
					//tell NameNode to gotohell
					Logger.log("Nothing available");
					return;
				}
				KeyListMessage klm = trm.LaunchInitTask( jarFileLocalPath, t.getMapperName(), blockLocalPath);

				Logger.log("out");
				Communicator.sendMessage(socket, klm);
				
				socket.close();
			}else if(inMessage.type.equals("MapperTask")){
				TaskMessage tm = (TaskMessage) inMessage;
				MapperTask t = (MapperTask) tm.task;
				String blockLocalPath = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + tm.fileName);
				
				
				String jarFileLocalPath = DataNode.rootPath.toString()+FileSystem.DIRECTORYSEPARATOR + t.getJob().getID() + ".jar";
				HDFSToLocal.MoveToLocal(jarFileLocalPath, t.getJarFilePath());
				
				TaskRunnerManager trm = DataNode.taskTrackerThread.getTaskRunnerManager(false);
				if(trm==null){
					//tell NameNode to gotohell
					return;
				}
				trm.LaunchMapperTask( jarFileLocalPath, t.getMapperName(), blockLocalPath, t.getSplits(), t.getJob().getID(), t.getTaskID());				
				socket.close();
				
			}else if(inMessage.type.equals("ReducerTask")){
				TaskMessage tm = (TaskMessage) inMessage;
				ReducerTask t = (ReducerTask) tm.task;
				
				String blockLocalPath = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + tm.fileName);				
				String jarFileLocalPath = DataNode.rootPath.toString()+FileSystem.DIRECTORYSEPARATOR + t.getJob().getID() + ".jar";
				HDFSToLocal.MoveToLocal(jarFileLocalPath, t.getJarFilePath());
				
				TaskRunnerManager trm = DataNode.taskTrackerThread.getTaskRunnerManager(false);
				if(trm==null){
					//tell NameNode to gotohell
					return;
				}
				String[] localPaths = new String[t.getClients().length];
				int iter = 0;
				for(String clientKey: t.getClients()){
					Message m = new Message("sendMeFile");
					m.fileName = "MAPPER_OUT_" + t.getJob().getID() + "_" + iter + "_" + t.getTaskID();
					
					
					localPaths[iter] = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + m.fileName + "_");
					Socket socket = Communicator.CreateTaskSocket(clientKey);
					Message fileSizeMessage = Communicator.sendAndReceiveMessage(socket, m);
					Communicator.receiveFile(socket, localPaths[iter], fileSizeMessage.fileSize);
									
					iter++;
				}
				//fill in the files
				trm.LaunchReducerTask( jarFileLocalPath, t.getReducerName(), localPaths, t.getJob().getID(), t.getTaskID());				
				socket.close();
			} else if(inMessage.type.equals("MergeAndUpload")){
				Logger.log("\n\n\n\nMerge message\n\n\n");
				MergeAndUploadMessage message = (MergeAndUploadMessage)inMessage;
				
				int iter = 0;
				String[] splitPaths = new String[message.clients.length];
				for(String clientKey: message.clients){
					Message m = new Message("sendMeFile");
					m.fileName = "REDUCER_OUT_" + message.jobId + "_" + (iter+splitPaths.length-1)%splitPaths.length;
					
					splitPaths[iter] = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + "SPLIT_" + message.jobId + "_" + iter);
					
					Socket socket = Communicator.CreateDataSocket(clientKey);
					Message fileSizeMessage = Communicator.sendAndReceiveMessage(socket, m);
					Communicator.receiveFile(socket, splitPaths[iter], fileSizeMessage.fileSize);
					iter++;
				}
				String finalOutputLocalPath = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + "FINAL_" + message.jobId);
				FileMerge.mergeFiles(splitPaths, finalOutputLocalPath);
				

     			new Thread(new LocalToHDFS(finalOutputLocalPath, message.HDFSFilePath)).start();
			}
				
				
		} catch (IOException|InterruptedException|ClassNotFoundException e) {
			Logger.log(e.getMessage());
			e.printStackTrace();
		} catch (InvalidDataNodeException e) {
			DataNode.reset();
		}

	}
}
