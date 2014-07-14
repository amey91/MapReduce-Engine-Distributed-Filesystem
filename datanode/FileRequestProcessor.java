package datanode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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

			Logger.log(inMessage.type );
			if(inMessage.type.equals("add")){

				Logger.log(inMessage.type );
				
				FileOutputStream fos = new FileOutputStream(DataNode.rootPath + (filesystem.FileSystem.DIRECTORYSEPARATOR +inMessage.fileName));
			    BufferedOutputStream bos = new BufferedOutputStream(fos);
			    

			    byte[] bytearray = new byte[1024];
			    InputStream is = socket.getInputStream();

			    long bytesLeft = inMessage.fileSize;
			    
			    while(bytesLeft>0){
			    	int bytesRead = is.read(bytearray, 0, bytearray.length);
			        bos.write(bytearray, 0, bytesRead);
			        System.out.println(" "+bytesRead);;
			        bytesLeft -= bytesRead;
				}
			    bos.close();
			    fos.close();
			    socket.close();
			    

			}
			else if(inMessage.type.equals("remove")){
				String blockName = inMessage.fileName;
				String blockPath = DataNode.rootPath+"/"+blockName;
				File fileTemp = new File(blockPath);
				if (fileTemp.exists()){
				    Logger.log("From delete File: " + inMessage.fileName);
					fileTemp.delete();
					DataNode.nameNode.ConfirmDeletion(blockName, DataNode.key);
				}
				else
					DataNode.nameNode.ConfirmDeletion(blockName, DataNode.key);
			}
		}
		catch(IOException | ClassNotFoundException | InterruptedException e){

		}

	}
}
