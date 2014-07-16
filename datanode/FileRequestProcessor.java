package datanode;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import namenode.InvalidDataNodeException;
import commons.Logger;
import communication.Communicator;
import communication.Message;

public class FileRequestProcessor extends Thread{
	Socket socket;

	public FileRequestProcessor(Object rorTable, Socket a){
		this.socket = a; 
	}

	@Override
	public void run(){
		try{
			Message inMessage = Communicator.receiveMessage(socket);
			Logger.log("received message" + inMessage.type);
			
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
		}
		catch(IOException | ClassNotFoundException | InterruptedException e){

		} catch (InvalidDataNodeException e) {
			DataNode.reset();
		}

	}
}
