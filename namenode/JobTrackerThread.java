package namenode;

import java.util.concurrent.ConcurrentHashMap;

import filesystem.FileSystemException;
import mapreduce.Job;

public class JobTrackerThread extends Thread {
	public Integer uniqueJobIdentifier;
	ConcurrentHashMap<Integer, JobTracker> runningJobs = new ConcurrentHashMap<Integer, JobTracker>();
	Object uniqueIDLock = new Object(); 
	
	JobTrackerThread(){
		uniqueJobIdentifier = 0;
	}
	public Integer addJob(Job newJob){
		Integer newJobId;
		synchronized (uniqueIDLock) {
			newJobId = uniqueJobIdentifier++;
		}
		JobTracker jt = null;
		try {
			jt = new JobTracker(newJobId, newJob);
		} catch (FileSystemException e) {
			// TODO if job has wrong params return 0->tell it to origin
			e.printStackTrace();
			return -1;
		}
		runningJobs.put(newJobId, jt);
		jt.run();
		return newJobId;
	}

	
}
