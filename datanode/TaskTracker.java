package datanode;


public class TaskTracker extends Thread {

	int totalProcesses;
	int freeProcesses;
	TaskRunnerManager[] taskRunnerManager;
	Object lock;
	
	public TaskTracker(){
		lock = new Object();
	}
	
	@Override
	public void run(){
		while(true){   
			try{
				int tempFreeProcesses = 0;
				
				for(int i =0; i< this.taskRunnerManager.length;i++){
					// we neglect processes whicha re not ready
					if(taskRunnerManager[i].isReady && !taskRunnerManager[i].isRunning)
						tempFreeProcesses++;
					//other wise the processes are busy, which is (total - running) 
				}
				synchronized(lock){
					freeProcesses = tempFreeProcesses;
				}
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public int getTotalProcesses(){
		return this.totalProcesses; 
	}

	public int getFreeProcesses(){
		synchronized(lock){
			return this.freeProcesses;
		}
	}

	public void register(int i, TaskRunnerManager newTaskRunnerManager) {
		taskRunnerManager[i] = newTaskRunnerManager;
	}

	public void initializeTaskRunnerManagerInterface(int numOfMappers) {
		taskRunnerManager=new TaskRunnerManager[numOfMappers];
		totalProcesses = numOfMappers;
	}
}
