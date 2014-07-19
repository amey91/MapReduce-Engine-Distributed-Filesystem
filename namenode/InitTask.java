package namenode;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import commons.Logger;
import communication.Communicator;
import communication.KeyListMessage;
import communication.Message;
import communication.TaskMessage;
import conf.Constants;
import filesystem.DistributedFile;
import filesystem.FileBlock;

public class InitTask extends Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2221023034328688071L;
	
	DistributedFile file;
	String jarFilePath;
	String mapperName;
	InitTask(JobTracker jobTracker, DistributedFile f, String jarFilePath, String mapperName) {
		super(jobTracker, 0);
		this.file = f;
		this.mapperName = mapperName;
		this.jarFilePath = jarFilePath;
	}
	
	public String getJarFile(){
		return jarFilePath;
	}
	
	public String getMapperName(){
		return mapperName;
	}

	// run partition values for keyset within 
	public Comparable<?>[] execute(){
		FileBlock[] blocks = file.getFileBlocks();
		long totalSizeEstimate = 0;
		KeyListMessage keys = null;
		// array of keys to be compared and partitioned
		Comparable<?> arr[][] = new Comparable<?>[blocks.length][];

		int iter = 0;
		int totalArrayLength = 0;
		for(FileBlock currBlock : blocks ){
			Socket socket;
			try {
				socket = Communicator.CreateTaskSocket(currBlock.getNodeLocations()[0]);
				TaskMessage m = new TaskMessage("InitTask", this);

				//block name actually
				m.fileName = currBlock.getBlockFileName();
				keys = (KeyListMessage)Communicator.sendAndReceiveMessage(socket, m);
				
				arr[iter] = keys.keyList;
				totalArrayLength += arr[iter].length;
				totalSizeEstimate += keys.outputSizeEstimate;
				
			} catch (IOException | InterruptedException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			iter++;
		}
		Comparable<?> mergedArray[] = new Comparable<?>[totalArrayLength];
		
		int loc = 0;
		for(int i=0; i<blocks.length; i++){
			System.arraycopy(mergedArray, loc, arr[i], 0, arr[i].length);
			loc+=arr[i].length;
		}
		Arrays.sort(mergedArray);
		
		
		int numberOfReducers = (int) (totalSizeEstimate / Constants.MAX_REDUCER_SIZE);
		
		Comparable<?>[] ranges = new Comparable<?>[numberOfReducers-1];
		int perSegmentSize = totalArrayLength/numberOfReducers;
		
		Logger.errLog("Number of Reducers = " + numberOfReducers);
		int index = perSegmentSize;
		for(int i=0; i < numberOfReducers - 1; i++){
			ranges[i] = mergedArray[index];
			index += perSegmentSize;
		}		
		return ranges;
	}
}
