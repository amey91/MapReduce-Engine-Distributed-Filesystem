package datanode;

import communication.Communicator;

public class TaskTracker extends Thread {
	// TODO watch youtube video from this guy -> https://www.youtube.com/watch?v=ziqx2hJY8Hg
	
	@Override
	public void run(){

		Communicator.listenForMessages(DataNode.jobSocket, null, JobRequestProcessor.class);
	}
}
