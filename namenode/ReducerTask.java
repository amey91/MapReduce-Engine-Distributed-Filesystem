package namenode;

import java.io.Serializable;


public class ReducerTask extends Task implements Serializable{

	private static final long serialVersionUID = -4477167297113637571L;

	ReducerTask(JobTracker parent, int taskId, Comparable<?> startKey, Comparable<?> endKey) {
		super(parent, taskId);
	}


	void execute() {
		// TODO Auto-generated method stub
		
	}

}
