package datanode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import namenode.NameNode;
import commons.AddressToIPPort;
import commons.Logger;
import communication.Communicator;
import communication.Message;
import conf.Constants;
import filesystem.FileBlock;
import filesystem.FileSystemException;

public class LocalToHDFS extends Thread {
	String localFilePath;
	String HDFSFilePath;

	public LocalToHDFS(String localFilePath, String hDFSFilePath) {
		this.localFilePath = localFilePath;
		this.HDFSFilePath = hDFSFilePath;
	}

	@Override
	public void run(){
		// referred to http://stackoverflow.com/questions/2149785/get-size-of-folder-or-file
		java.io.File file = new java.io.File(localFilePath);

		FileBlock[] fileBlocks = null;
		int no_of_blocks = 1;
		try {
				
			fileBlocks = DataNode.nameNode.localToHDFS(DataNode.key, HDFSFilePath, file.length());
			no_of_blocks =  fileBlocks.length;
			
			Logger.log("File block received by origin: ");
			for(FileBlock f: fileBlocks){
				String[] res = f.getNodeLocations();
				Logger.log("BlockFileName:"+f.getBlockFileName()+ " | Nodes: "+res[0]+"|"+res[1]+"|"+res[2]);
			}

			// divide the file into smaller blocks
			long[] splitSizes = getDivisionSizes(localFilePath, no_of_blocks);
			DataNode.ftThread.add(localFilePath, HDFSFilePath, fileBlocks, splitSizes);
			
			//long[] result = divideAndSendFile(localFilePath, no_of_blocks, fileBlocks);
			//DataNode.nameNode.confirmLocalToHDFS(DataNode.key, HDFSFilePath, fileBlocks, result);
			
		} catch (FileSystemException | IOException e) {
			// TODO delete 
			Logger.log(e.getMessage());
			e.printStackTrace();
		}
	}

	private long[] getDivisionSizes(String localFilePath, int no_of_blocks) throws IOException{
		long[] divisions = new long[no_of_blocks];

		File file = new File(localFilePath);
		long totalLength = file.length();
		long idealSize = totalLength/no_of_blocks;
		long startLoc = 0;
		int divNo = 0;

		InputStream is = new FileInputStream(file);

		divisions[no_of_blocks -1] = totalLength;
		while(divNo<no_of_blocks-1){
			long newLoc = startLoc + idealSize;
			is.skip(idealSize);
			while(is.read()!='\n')
				newLoc++;

			divisions[divNo] = newLoc - startLoc; 

			divisions[no_of_blocks-1] -= divisions[divNo];
			startLoc = newLoc;
			Logger.log("div" + divNo+": " + divisions[divNo]);
			divNo++;
		}

		Logger.log("div" + divNo+": " + divisions[divNo]);
		is.close();
		
		return divisions;
	}

	private long[] divideAndSendFile(String localFilePath, int no_of_blocks, FileBlock[] fileBlocks) throws IOException, InterruptedException {

		File localFile = new File(localFilePath);

	    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFile));

		long[] fileSizes = getDivisionSizes(localFilePath, no_of_blocks);
		
		for(int block = 0; block<no_of_blocks;block++)
		{
			Socket socket [] = new Socket[Constants.REPLICATION_FACTOR]; 

			String[] dataNodeLocations = fileBlocks[block].getNodeLocations();
			// write each block to its destination
			for(int i=0;i<Constants.REPLICATION_FACTOR;i++){
				String[] ipPort = AddressToIPPort.addressToIPPort(dataNodeLocations[i]);
				
				Logger.log("sending message: "+ipPort[0] + Integer.parseInt(ipPort[1]));
				socket[i] = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
				Message sendMessage = new Message("add");
				sendMessage.fileName = fileBlocks[block].getBlockFileName();
				sendMessage.fileSize = fileSizes[block];

				Communicator.sendMessage(socket[i], sendMessage);
			}
			

			fileSizes[block] = Communicator.sendStream(socket, bis, fileSizes[block]);
			
		}
		bis.close();
		return fileSizes;
	}
}
