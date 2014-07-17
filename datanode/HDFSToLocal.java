package datanode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import namenode.InvalidDataNodeException;
import commons.FileMerge;
import commons.Logger;
import communication.Communicator;
import communication.Message;
import filesystem.FileBlock;
import filesystem.FileSystem;
import filesystem.FileSystemException;

public class HDFSToLocal extends Thread {
	String localFilePath;
	String HDFSFilePath;

	public HDFSToLocal(String localFilePath, String hDFSFilePath) {
		this.localFilePath = localFilePath;
		this.HDFSFilePath = hDFSFilePath;
	}

	@Override
	public void run(){
		// referred to http://stackoverflow.com/questions/2149785/get-size-of-folder-or-file

		try {

			try{
				FileOutputStream fos = new FileOutputStream(localFilePath);
				fos.close();
			}catch(FileNotFoundException e){
				Logger.log("Invalid output file location:"+ localFilePath);
				return;
			}
			
			
			FileBlock[] fileBlocks = DataNode.nameNode.getFileBlocks(DataNode.key, HDFSFilePath);
			if(fileBlocks==null){
				Logger.log("Invalid input file location: "+ HDFSFilePath);
				return;
			}
				
			int counter = 0;
			String files[] = new String[fileBlocks.length];
			
			for(FileBlock block: fileBlocks){
				
				Boolean success = false;
				
				
				String tempFileName = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR +"__TEMP" + counter);
				files[counter] = tempFileName;
				
				for(String location: block.getNodeLocations())
				{
					try{
						Message m = new Message("sendMeFile");
						m.fileName = block.getBlockFileName();
						m.sendLocation = null;
						
						Socket socket = Communicator.CreateDataSocket(location);
						Communicator.sendMessage(socket, m);
						if(Communicator.receiveFile(socket, tempFileName, block.getSize())!= block.getSize())
							throw new IOException("Received file size not expected");
						socket.close();
						success = true;
						break;
					}
					catch(IOException | InterruptedException e) {
						Logger.log("one node failed! trying another: " + e.getMessage());
						// TODO delete
						e.printStackTrace();
					}
				}
				
				if(success==false)
					throw new IOException("File Copy Failed! Try again!");
				counter++;
			}
			
			FileMerge.mergeFiles(files, localFilePath);
		} catch (IOException|FileSystemException e) {
			// TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		} catch (InvalidDataNodeException e) {
			DataNode.reset();
		}
	}
}
