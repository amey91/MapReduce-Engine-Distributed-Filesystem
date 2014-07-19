package taskrunner;

import java.io.IOException;
import java.net.Socket;

import commons.Logger;
import communication.Communicator;
import communication.MapperTaskMessage;
import communication.Message;

public class TaskRequestProcessor extends Thread {
	
	Socket socket;
	public TaskRequestProcessor(Object rorTable, Socket a){
		this.socket = a; 
	}

	@Override
	public void run(){
		try{
			Message inMessage = Communicator.receiveMessage(socket);
			Logger.log("received message: " + inMessage.type);
			
			switch(inMessage.type){
			case("InitTask"):
				MapperTaskMessage mtm = (MapperTaskMessage) inMessage;
				RunMapper rMapper  = new RunMapper(mtm.jarFileLocalPath, mtm.mapperClassName, mtm.blockLocalPath, 
						mtm.outputPath, true, TaskRunner.dataNodeListeningPort);
				break;

			case("Mapper"):
				mtm = (MapperTaskMessage) inMessage;
				rMapper  = new RunMapper(mtm.jarFileLocalPath, mtm.mapperClassName, mtm.blockLocalPath, 
						mtm.outputPath, false, TaskRunner.dataNodeListeningPort);
				break;

			case("Reducer"):
				
				break;
			
			default:
				new RunReducer();
				break;
			}
			
			
		}catch(IOException | ClassNotFoundException | InterruptedException e){
			
		}
	}		
}
