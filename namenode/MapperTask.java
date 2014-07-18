package namenode;

import filesystem.FileBlock;
import java.io.Serializable;

// one type of task. See Task.java
public class MapperTask extends Task implements Serializable{

	private static final long serialVersionUID = -8439626879678851485L;
	MapperTask(JobTracker jobTracker, int taskId, FileBlock fb) {
		super(jobTracker, taskId);
		
	}
	void run(){
		
	}
	void report(Boolean success){
		
	}

	void execute() {
		// TODO Auto-generated method stub
		
	}
}
