package datanode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import namenode.InvalidDataNodeException;

import commons.Logger;

import filesystem.FileBlock;
import filesystem.FileSystemException;


// takes a file on  local filesystem and puts it into 
// my DFS. 
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
			// number of file blocks are already decided by namenode 
			fileBlocks = DataNode.nameNode.localToHDFS(DataNode.key, HDFSFilePath, file.length());
			no_of_blocks =  fileBlocks.length;
			
			Logger.log("File block received by origin: ");
			for(FileBlock f: fileBlocks){
				String[] res = f.getNodeLocations();
				Logger.log("BlockFileName:"+f.getBlockFileName()+ " | Nodes: "+res[0]+"|"+res[1]+"|"+res[2]);
			}

			// decide size of smaller blocks
			long[] splitSizes = getDivisionSizes(localFilePath, no_of_blocks);
			
			// copy these files to the other locations
			DataNode.fcThread.add(localFilePath, HDFSFilePath, fileBlocks, splitSizes);
			
			//long[] result = divideAndSendFile(localFilePath, no_of_blocks, fileBlocks);
			//DataNode.nameNode.confirmLocalToHDFS(DataNode.key, HDFSFilePath, fileBlocks, result);
			
		} catch (FileSystemException | IOException e) {
			// TODO delete 
			Logger.log(e.getMessage());
			e.printStackTrace();
		} catch (InvalidDataNodeException e) {
			DataNode.reset();
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
}
