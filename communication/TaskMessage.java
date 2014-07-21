package communication;

import namenode.Task;


//type of message
public class TaskMessage extends Message{

	private static final long serialVersionUID = -2936828532419035077L;
	
	public Task task;
	
	// tasktype can be inittask, maptask, reducetask
	public TaskMessage(String taskType, Task task) {
		super(taskType);
		this.task = task;
	}



}
