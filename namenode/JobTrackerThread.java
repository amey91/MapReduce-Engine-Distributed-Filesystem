package namenode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import commons.Logger;
import filesystem.FileSystemException;
import mapreduce.Job;

public class JobTrackerThread extends Thread {
	public Integer uniqueJobIdentifier;
	ConcurrentHashMap<Integer, JobTracker> runningJobs = new ConcurrentHashMap<Integer, JobTracker>();
	Object uniqueIDLock = new Object(); 
	ConcurrentLinkedQueue<JobTracker> tempJobs = new ConcurrentLinkedQueue<JobTracker>();

	JobTrackerThread(){
		uniqueJobIdentifier = 0;
	}
	public Integer addJob(Job newJob) throws FileSystemException {
		
		Integer newJobId;
		synchronized (uniqueIDLock) {
			newJobId = uniqueJobIdentifier++;
		}
		JobTracker jt = new JobTracker(newJobId, newJob);
		tempJobs.add(jt);
		return newJobId;
	}


	@Override 
	public void run(){
		while(true){
			try{
				for(JobTracker jt : tempJobs){
					jt.run();
					Logger.log(jt.jobName + " - run called");
					runningJobs.put(jt.jobId, jt);
					tempJobs.remove(jt);
				}
				Thread.sleep(2000);

			}catch(Exception e){

			}
		}
	}


	public void displayRunningJobs(){
		if(runningJobs.size()==0){
			Logger.log("No jobs running at this time");
			//return
		}

		for(Integer key : runningJobs.keySet()){
			Logger.log(runningJobs.get(key).toString());
		}

		for(JobTracker jt : tempJobs){
			Logger.log("tempjob: "+jt);
		}
	}

	public void sendUpdate(String clientKey, Boolean isMapper, int jobId, int taskId,
			double percentComplete, Boolean complete) {
		JobTracker jobTracker = runningJobs.get(jobId);
		if(jobTracker == null){
			Logger.log(jobTracker.toString() + " is not in running jobs list");
			return;
		}
		jobTracker.report(clientKey, isMapper, taskId, percentComplete, complete);
	}

}
