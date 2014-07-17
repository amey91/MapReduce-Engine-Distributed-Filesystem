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
			}
			else if(inMessage.type.equals("reset")){
				DataNode.resetAllThreads();
			}
			else if(inMessage.type.equals("sendFile")){
				
				Socket outSocket = null;
				String location = inMessage.sendLocation;
				try{
					String blockName = inMessage.fileName;
					if(location!=null)
						outSocket = Communicator.CreateDataSocket(location);
					else
						outSocket = socket;
					File sendFile = new File(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName));
					BufferedInputStream bis = new BufferedInputStream(
							new FileInputStream(DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR + blockName)));
					
					if(location!=null){
						Message m = new Message("add");
						m.fileName = blockName;
						m.fileSize = sendFile.length();
						Communicator.sendMessage(outSocket, m);
					}
					Message confirmation = new Message("success");
					
					if(Communicator.sendStream(outSocket, bis, sendFile.length())!=sendFile.length()){
						confirmation.type = "fail";
					}
					
					if(location!=null)
						Communicator.sendMessage(socket, confirmation);
					
					outSocket.close();
				}catch(IOException | InterruptedException e){
					try {
						
						Logger.log(e.getMessage());
						e.printStackTrace();
						if(location!=null){
							Message confirmation = new Message("fail");
							Communicator.sendMessage(socket, confirmation);
							outSocket.close();
						}
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
