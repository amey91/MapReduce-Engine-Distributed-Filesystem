package datanode;

import java.io.File;
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
		java.io.File file = new java.io.File(localFilePath);
		if(!file.canWrite()){
			Logger.log("Invalid output file location");
			return;
		}

		try {
			
			FileBlock[] fileBlocks = DataNode.nameNode.getFileBlocks(DataNode.key, HDFSFilePath);
			
			int counter = 0;
			String files[] = new String[fileBlocks.length];
			
			for(FileBlock block: fileBlocks){
				
				Boolean success = false;
				
				
				String tempFileName = DataNode.rootPath + (FileSystem.DIRECTORYSEPARATOR +"__TEMP" + counter);
				files[counter] = tempFileName;
				
				for(String location: block.getNodeLocations())
				{
					try{
						Message m = new Message("sendFile");
						m.fileName = block.getBlockFileName();
						
						Socket socket = Communicator.CreateDataSocket(location);
						Communicator.sendMessage(socket, m);
						if(Communicator.receiveFile(socket, tempFileName, block.getSize())!= block.getSize())
							throw new IOException("Received file size not expected");
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
