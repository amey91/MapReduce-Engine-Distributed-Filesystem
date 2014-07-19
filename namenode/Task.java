package namenode;

import java.io.Serializable;

public abstract class Task implements Serializable{
	/**
	 *  Types of Tasks:
	 *  InitTask, MapperTask, ReducerTask
	 */
	private static final long serialVersionUID = -4870229257017356666L;
	
	JobTracker parent;
	int taskId;
	
	Task(JobTracker jobTracker, int taskId){
		this.parent = jobTracker;
		this.taskId = taskId;
	}
	public int getTaskID(){
		return taskId;
	}
	public JobTracker getJob(){
		return parent;
	}
	void reportCompletion(Boolean success){
		parent.report(success, this);
	}
	
	public String getJarFilePath(){
		return parent.getJarFilePath();
	}

}
