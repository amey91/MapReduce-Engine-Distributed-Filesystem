package taskrunner;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import commons.Logger;
import communication.Communicator;
import communication.HeartbeatMessage;

public class TaskRunnerHeartBeatThread extends Thread {
	Socket heartBeatSocket;
	int dataNodeListeningPort;
	RunReducer parentReducer;
	RunMapper parentMapper;

	public TaskRunnerHeartBeatThread(Object runner, int dataNodeListeningPort) throws UnknownHostException, IOException {
		this.dataNodeListeningPort = dataNodeListeningPort;
		
		this.parentMapper = null;
		this.parentReducer = null;
		if(runner instanceof RunMapper)
			this.parentMapper = (RunMapper) runner;
		else if(runner instanceof RunReducer)
			this.parentReducer = (RunReducer)runner;
	}

	@Override
	public void run(){
		while(true){
			try{
				this.heartBeatSocket = new Socket("127.0.0.1",dataNodeListeningPort);
				double currPercent = (this.parentMapper==null) ? this.parentReducer.percent : this.parentMapper.percent;
				if(currPercent == 100 ){
					//thread is complete
					HeartbeatMessage message = new HeartbeatMessage(100, true);
					Communicator.sendMessage(heartBeatSocket, message);
					break;
				}
				HeartbeatMessage message = new HeartbeatMessage(currPercent, false);
				Communicator.sendMessage(heartBeatSocket, message);
				Thread.sleep(1000);}
			catch(Exception e){
				Logger.log("Error in task runner heartbeat.");
				e.printStackTrace();
			}
		}//end of while
	}
}
