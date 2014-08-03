package namenode;

import java.util.concurrent.ConcurrentLinkedQueue;

import commons.Logger;

public class TaskQueueThread extends Thread {
	public ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();

	@Override
	public void run(){
		while(true){
			int result=-1;
			// for each thread
			// check if namenodelocation is available
			for(Task t : taskQueue){
				
				try {
					if(t instanceof MapperTask){
						result = ((MapperTask) t).performOperation();
					}
					else if(t instanceof ReducerTask){
						result = ((ReducerTask) t).performOperation();
					}
				} finally{
					if(result==0)
						taskQueue.remove(t);
					else 
						Logger.log("Error while executing task " + t.taskId);
				}
			}
			

			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				Logger.log("Task Queue interrupted");
			}
		}

	}

	public void addJob(Task task){

		// task can be mappertask/reducertask/inittask
		taskQueue.add(task);

		//this.jobQueue.add(task);

	}
}
