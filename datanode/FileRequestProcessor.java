package datanode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import namenode.InvalidDataNodeException;
import commons.Logger;
import communication.Communicator;
import communication.Message;
import filesystem.FileSystem;

public class FileRequestProcessor extends Thread{
	Socket socket;

	public FileRequestProcessor(Object rorTable, Socket a){
		this.socket = a; 
	}

	@Override
	public void run(){
		try{
			Message inMessage = Communicator.receiveMessage(socket);
			Logger.log("received message: " + inMessage.type);
			
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
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName)));
				
				Communicator.sendStream(socket, bis, sendFile.length());
				socket.close();
			}
			else if(inMessage.type.equals("sendFile")){
				
				try{
					String blockName = inMessage.fileName;
					Socket outSocket = Communicator.CreateDataSocket(inMessage.sendLocation);
					
					File sendFile = new File(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName));
					BufferedInputStream bis = new BufferedInputStream(
							new FileInputStream(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName)));
					
					
					Message m = new Message("add");
					m.fileName = blockName;
					m.fileSize = sendFile.length();
					Communicator.sendMessage(outSocket, m);
					
					Boolean success = Communicator.sendStream(outSocket, bis, sendFile.length())==sendFile.length();
						
					
					Message confirmation = new Message(success?"success":"fail");
					
					Communicator.sendMessage(socket, confirmation);
					
					outSocket.close();
					socket.close();
				}catch(IOException | InterruptedException e){
					try {
						
						Logger.log(e.getMessage());
						e.printStackTrace();
						
						Message confirmation = new Message("fail");
						Communicator.sendMessage(socket, confirmation);
						outSocket.close();
						
					} catch (InterruptedException | IOException e1) {
						Logger.log("Oh well!");
						e1.printStackTrace();
					}
				}
			}
		} catch (IOException|InterruptedException|ClassNotFoundException e) {
			//TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		} catch (InvalidDataNodeException e) {
			DataNode.reset();
		}

	}
}
