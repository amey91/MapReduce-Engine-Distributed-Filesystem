package namenode;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import mapreduce.Job;
import commons.Logger;
import communication.Communicator;
import communication.MergeAndUploadMessage;
import communication.Message;
import filesystem.Directory;
import filesystem.FileBlock;
import filesystem.FileSystem;
import filesystem.FileSystemException;


// track the jobs on datanodes
public class JobTracker implements Serializable{

	private static final long serialVersionUID = -1064895292740185904L;

	String mapperClassName;
	String reducerClassName;
	String inputPath;
	String outputPath;
	String jobName;
	String jarFilePath;
	String datanodeKey; 
	String reportLock = new String();
	Integer jobId;

	InitTask initTask;
	MapperTask[] mapperTaskArray;
	ReducerTask[] reducerTaskArray;

	int numMapperTasksComplete;
	Boolean[] mapperTaskSuccessArray;
	Boolean[] reducerTaskSuccessArray;

	double[] mapperTaskCompletion;
	double[] reducerTaskCompletion;

	Boolean mapperPhaseComplete = false;
	int numberOfMappers;

	String[] reducerClients;

	private void splitMapperTasksJobs() throws FileSystemException{
		FileBlock[] blocks = NameNode.fs.getFileBlocks(inputPath); 
		numberOfMappers = blocks.length;


		mapperTaskArray = new MapperTask[numberOfMappers];
		for(int i=0; i < numberOfMappers; i++)
			mapperTaskArray[i] = new MapperTask(this, i, blocks[i]);

		mapperTaskSuccessArray = new Boolean[numberOfMappers];
		mapperTaskCompletion = new double[numberOfMappers];
		for(int i=0; i< mapperTaskSuccessArray.length;i++){
			mapperTaskSuccessArray[i] = false;
			mapperTaskCompletion[i] = 0;
		}
	}
	public JobTracker(Integer jobId, Job job) throws FileSystemException {
		this.jobId = jobId;
		this.datanodeKey = job.datanodeKey;
		this.jobName = job.jobName;
		this.mapperClassName = job.mapperClassName;
		this.reducerClassName = job.reducerClassName;
		this.inputPath = job.inputPath;
		this.outputPath = job.outputPath;
		this.jarFilePath = job.jarFile;


		initTask = new InitTask(this, inputPath, job.jarFile, mapperClassName);
		splitMapperTasksJobs();
	}

	public void run(){
		Logger.log("IN run");
		Comparable<?>[] splits = null;
		try {
			splits = initTask.execute();
		} catch (FileSystemException e) {
			//cannot happen checked file in constructor
			// TODO delete
			e.printStackTrace();
		}
		int numberOfReducers = splits.length + 1;

		reducerTaskArray = new ReducerTask[numberOfReducers];
		reducerClients = new String[numberOfReducers];
		for(int i=0; i < numberOfReducers; i++){
			if(i==0)
				reducerTaskArray[i] = new ReducerTask(this, i, null, splits[i], numberOfMappers);
			else if(i==numberOfReducers-1)
				reducerTaskArray[i] = new ReducerTask(this, i, splits[i-1], null, numberOfMappers);
			else
				reducerTaskArray[i] = new ReducerTask(this, i, splits[i-1], splits[i], numberOfMappers);
		}

		reducerTaskSuccessArray = new Boolean[numberOfReducers];
		reducerTaskCompletion = new double[numberOfReducers];
		for(int i=0; i < numberOfReducers; i++){
			reducerTaskSuccessArray[i] = false;
			reducerTaskCompletion[i] = 0.0;
		}

		numMapperTasksComplete = 0;
		for(int i=0; i < mapperTaskArray.length; i++){
			try {
				mapperTaskArray[i].setSplits(splits);
				mapperTaskArray[i].execute();
			} catch (InvalidDataNodeException e) {
				e.printStackTrace();
				handleFailure();
			}
		}
	}
	private void handleFailure() {
		// TODO Auto-generated method stub

	}
	public int getID() {
		return jobId;
	}
	public String getJarFilePath() {
		return jarFilePath;
	}

	@Override
	public String toString(){
		return "JobID: " + this.jobId + " Name: " + this.jobName + " Running on: " + this.datanodeKey;
	}
	public void report(String clientKey, Boolean isMapper, int taskId,
			double percentComplete, Boolean complete) {

		synchronized(reportLock){

			if(!mapperPhaseComplete && isMapper){

				mapperTaskCompletion[taskId] = percentComplete;
				if(complete){
					
					for(DataNodeInfo d: NameNode.instance.dataNodeList)
						if(d.getId().equals(clientKey))
							d.removeRunningTask(mapperTaskArray[taskId]);
						
						
					mapperTaskSuccessArray[taskId] = true;

					Boolean isComplete = true;
					for(Boolean b: mapperTaskSuccessArray)
						if(!b)
							isComplete = false;

					mapperPhaseComplete = isComplete;

					for(ReducerTask reducer: reducerTaskArray){
						reducer.addProvider(taskId, clientKey);
						if(mapperPhaseComplete){
							try {
								reducer.execute();
							} catch (InvalidDataNodeException e) {
								e.printStackTrace();
								handleFailure();
							}
						}
					}
				}
			}
			else if(mapperPhaseComplete && !isMapper){
				reducerTaskCompletion[taskId] = percentComplete;
				if(complete){
					for(DataNodeInfo d: NameNode.instance.dataNodeList)
						if(d.getId().equals(clientKey))
							d.removeRunningTask(reducerTaskArray[taskId]);
					
					reducerTaskSuccessArray[taskId] = true;
					Logger.log(taskId+clientKey);
					reducerClients[taskId] = clientKey;
					Boolean isComplete = true;
					for(Boolean b: reducerTaskSuccessArray)
						if(!b)
							isComplete = false;

					if(isComplete){
						MergeAndUploadMessage m = new MergeAndUploadMessage(jobId, reducerClients, 
								outputPath + FileSystem.DIRECTORYSEPARATOR + "out");


						try {
							Socket sendingSocket = Communicator.CreateTaskSocket(reducerClients[0]);
							Communicator.sendMessage(sendingSocket, m);
							sendingSocket.close();
							//TODO tell all nodes to clean the job files
						} catch (IOException e) {
							e.printStackTrace();
							handleFailure();
						}
						NameNode.instance.jtThread.remove(jobId);
					}
				}
			}
			else
				Logger.log("Phase of Job Tracker is wrong");
		}//end of report
	}
}
