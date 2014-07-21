package namenode;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import commons.Logger;
import communication.Communicator;
import communication.KeyListMessage;
import communication.TaskMessage;
import conf.Constants;
import filesystem.DistributedFile;
import filesystem.FileBlock;
import filesystem.FileSystemException;


// start a task related to a job
public class InitTask extends Task {

	private static final long serialVersionUID = -2221023034328688071L;
	
	String inputFilePath;
	String jarFilePath;
	String mapperName;
	InitTask(JobTracker jobTracker, String inputFilePath, String jarFilePath, String mapperName) {
		super(jobTracker, 0);
		this.inputFilePath = inputFilePath;
		this.mapperName = mapperName;
		this.jarFilePath = jarFilePath;
	}
	
	public String getMapperName(){
		return mapperName;
	}

	// run partition values for keyset within 
	public Comparable<?>[] execute() throws FileSystemException{
		FileBlock[] blocks = NameNode.fs.getFileBlocks(inputFilePath);
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
				
				//for(Comparable<?> c:arr[iter])
					//Logger.log((String)c);
			} catch (IOException | InterruptedException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			iter++;
		}
		Comparable<?>[] mergedArray = new Comparable<?>[totalArrayLength];
		
		int loc = 0;
		for(int i=0; i<blocks.length; i++){
			for(Comparable<?> c:arr[i])
				mergedArray[loc++] = c;
		}
		
		Arrays.sort(mergedArray);
		
		
		int numberOfReducers = Math.min( (int) (totalSizeEstimate / Constants.MAX_REDUCER_SIZE), NameNode.instance.dataNodeList.size());
		
		Comparable<?>[] ranges = new Comparable<?>[numberOfReducers-1];
		int perSegmentSize = totalArrayLength/numberOfReducers;
		
		Logger.log("Number of Reducers = " + numberOfReducers);
		int index = perSegmentSize;
		for(int i=0; i < numberOfReducers - 1; i++){
			ranges[i] = mergedArray[index];
			index += perSegmentSize;
		}		
		return ranges;
	}
}
