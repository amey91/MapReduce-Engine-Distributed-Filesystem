package namenode;

import filesystem.Directory;
import filesystem.DistributedFile;
import filesystem.FileBlock;
import filesystem.FileSystemException;
import mapreduce.Job;

import java.io.Serializable;


// track the jobs on datanodes
public class JobTracker implements Serializable{

	private static final long serialVersionUID = -1064895292740185904L;
	
	String mapperClassName;
	String reducerClassName;
	DistributedFile inputFile;
	Directory outputPath;
	String jobName;
	String jarFilePath;
	String datanodeKey; 
	
	Integer jobId;
	
	InitTask initTask;
	MapperTask[] mapperTaskArray;
	ReducerTask[] reducerTaskArray;
	
	int numMapperTasksComplete;
	int[] mapperTaskSuccessArray;
	int[] reducerTaskSuccessArray;
	
	private void splitJobs(){
		//TODO use smarter algo
		FileBlock[] blocks = inputFile.getFileBlocks(); 
		int numMapperTasks = blocks.length;
		
		
		mapperTaskArray = new MapperTask[numMapperTasks];
		for(int i=0; i < numMapperTasks; i++)
			mapperTaskArray[i] = new MapperTask(this, i, blocks[i]);
		int[] mapperTaskSuccessArray = new int[numMapperTasks];
		for(int success: mapperTaskSuccessArray)
			success = 0;
	}
	public JobTracker(Integer jobId, Job job) throws FileSystemException {
		this.jobId = jobId;
		this.datanodeKey = job.datanodeKey;
		this.jobName = job.jobName;
		this.mapperClassName = job.mapperClassName;
		this.reducerClassName = job.reducerClassName;
		this.inputFile = NameNode.fs.getFile(job.inputPath);
		this.outputPath = NameNode.fs.getDirectory(job.outputPath);
		this.jarFilePath = job.jarFile;
		

		initTask = new InitTask(this, inputFile, job.jarFile, mapperClassName);
		splitJobs();
	}
	void report(Boolean success, Task task){
		if(task instanceof MapperTask){
			mapperTaskSuccessArray[task.taskId] = success?1:-1;
			//when complete launch reducers
		}
		else if(task instanceof ReducerTask){
			reducerTaskSuccessArray[task.taskId] = success?1:-1;
		}
	}
	public void run(){
		
		Comparable<?>[] splits = initTask.execute();
		int numberOfReducers = splits.length + 1;

		reducerTaskArray = new ReducerTask[numberOfReducers];
		
		for(int i=0; i < numberOfReducers; i++){
			if(i==0)
				reducerTaskArray[i] = new ReducerTask(this, i, null, splits[i]);
			else if(i==numberOfReducers-1)
				reducerTaskArray[i] = new ReducerTask(this, i, splits[i-1], null);
			else
				reducerTaskArray[i] = new ReducerTask(this, i, splits[i-1], splits[i]);
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
}
