package datanode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
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
				//TODO
			}
		}
		catch(IOException | ClassNotFoundException | InterruptedException e){

		}

	}
}
