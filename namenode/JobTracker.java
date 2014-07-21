package namenode;

import filesystem.Directory;
import filesystem.DistributedFile;
import filesystem.FileBlock;
import filesystem.FileSystemException;
import mapreduce.Job;

import java.io.Serializable;

import commons.Logger;


// track the jobs on datanodes
public class JobTracker implements Serializable{

	private static final long serialVersionUID = -1064895292740185904L;
	
	String mapperClassName;
	String reducerClassName;
	String inputPath;
	Directory outputPath;
	String jobName;
	String jarFilePath;
	String datanodeKey; 
	
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
		this.outputPath = NameNode.fs.getDirectory(job.outputPath);
		this.jarFilePath = job.jarFile;
		

		initTask = new InitTask(this, inputPath, job.jarFile, mapperClassName);
		splitMapperTasksJobs();
	}

	public void run(){
		
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
		for(int i=0; i < mapperTaskArray.length; i++)
			mapperTaskArray[i].execute();
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

		//TODO sync
		
		if(!mapperPhaseComplete && isMapper){
			
			mapperTaskCompletion[taskId] = percentComplete;
			if(complete){
				mapperTaskSuccessArray[taskId] = true;
			
				Boolean isComplete = true;
				for(Boolean b: mapperTaskSuccessArray)
					if(!b)
						isComplete = false;
				
				mapperPhaseComplete = isComplete;
				
				for(ReducerTask reducer: reducerTaskArray){
					reducer.addProvider(taskId, clientKey);
					if(mapperPhaseComplete)
						reducer.execute();
				}
			}
		}
		else if(mapperPhaseComplete && !isMapper){
			reducerTaskCompletion[taskId] = percentComplete;
			if(complete){
				mapperTaskSuccessArray[taskId] = true;
			
				Boolean isComplete = true;
				for(Boolean b: mapperTaskSuccessArray)
					if(!b)
						isComplete = false;
				
				if(isComplete){
					//remove from jobTracker
				}
			}
		}
		else
			Logger.log("Phase of Job Tracker is wrong");
	}
}
