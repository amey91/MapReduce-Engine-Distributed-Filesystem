package namenode;

import java.util.concurrent.ConcurrentHashMap;

import mapreduce.Job;

public class JobTrackerThread extends Thread {
	public Integer uniqueJobIdentifier;
	ConcurrentHashMap<Integer, Job> runningJobs;
	Object uniqueIDLock = new Object(); 
	
	JobTrackerThread(){
		uniqueJobIdentifier = 0;
	}
	public Integer addJob(Job newJob){
		Integer newJobId;
		synchronized (uniqueIDLock) {
			newJobId = uniqueJobIdentifier++;
		}
		runningJobs.put(newJobId, newJob);
		return newJobId;
	}

	
}
