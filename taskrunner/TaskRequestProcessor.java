package taskrunner;

import java.io.IOException;
import java.net.Socket;

import commons.Logger;
import communication.Communicator;
import communication.MapperTaskMessage;
import communication.Message;
import communication.ReducerTaskMessage;

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
				RunInit rInit  = new RunInit(mtm.jarFileLocalPath, mtm.mapperClassName, 
						mtm.blockLocalPath);
				
				rInit.Run(socket);
				break;

			case("MapperTask"):
				mtm = (MapperTaskMessage) inMessage;
				RunMapper rMapper  = new RunMapper(mtm.jarFileLocalPath, mtm.mapperClassName, mtm.blockLocalPath, 
						mtm.outputLocalPath, TaskRunner.dataNodeListeningPort);
				rMapper.Run();
				break;

			case("ReducerTask"):
				ReducerTaskMessage rtm = (ReducerTaskMessage) inMessage;
				RunReducer rReducer = new RunReducer(rtm.jarFileLocalPath, rtm.reducerClassName, rtm.localInputPaths, rtm.outputLocalPath);
				rReducer.Run();
				break;
			
			default:
				Logger.log("Wrong message");
				break;
			}
			
			
		}catch(IOException | ClassNotFoundException | InterruptedException e){
			
		} catch (Exception e) {
			Logger.log("Error while initializing Mapper/Reducer task:");
			e.printStackTrace();
		}
	}		
}
