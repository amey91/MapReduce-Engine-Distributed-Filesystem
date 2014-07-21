package taskrunner;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import commons.Logger;
import communication.Communicator;
import communication.HeartbeatMessage;
import jarmanager.JarLoader;
import mapreduce.Context;
import mapreduce.Mapper;


// run a mapper task for the specified file block
public class RunMapper {
	
	Class<Mapper> mapperClass;
	Boolean isInitTask;
	String blockLocalPath;
	String outputLocalPath;
	int dataNodeListeningPort;
	
	public RunMapper(String jarFileLocalPath, String mapperClassName, String blockLocalPath, 
			String outputLocalPath, int dataNodeListeningPort) throws Exception{

		this.mapperClass = (Class<Mapper>) JarLoader.getClassFromJar(jarFileLocalPath, mapperClassName);
		this.blockLocalPath = blockLocalPath;
		this.outputLocalPath = outputLocalPath;
		this.dataNodeListeningPort = dataNodeListeningPort;
	}
	
	public void Run(){
		
		try {
			Mapper m = mapperClass.newInstance();
			Logger.log("dataNodeListeningPort = "+dataNodeListeningPort);
	
			Socket heartBeatSocket = new Socket("127.0.0.1", dataNodeListeningPort);

			File file = new File(blockLocalPath, "r");
			long fileLength = file.length();
			
			InputStream    fis = new FileInputStream(blockLocalPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			

			Context context = new Context(outputLocalPath);
			
			String line;
			int lineNumber = 0;
			
			long lastTime = System.currentTimeMillis();
			
			
			long totalRead = 0;
			while ((line = br.readLine()) != null) {
			    m.map(lineNumber, line, context);
			    lineNumber++;
			    totalRead += line.length();
			    
			    //TODO move this to another thread
			    if(System.currentTimeMillis() - lastTime >1000 ){
			    	lastTime = System.currentTimeMillis();
			    	double percent = (totalRead*1.0)/fileLength;
			    	HeartbeatMessage message = new HeartbeatMessage(percent, false);
			    	Communicator.sendMessage(heartBeatSocket, message);
			    }
			}
			context.dumpToFile();

	    	HeartbeatMessage message = new HeartbeatMessage(100, true);
	    	Communicator.sendMessage(heartBeatSocket, message);
	    	
			br.close();
			br = null;
			fis = null;	
		} catch (InstantiationException|IllegalAccessException|IOException|InterruptedException e) {
			// TODO Auto-generated catch block
			Logger.log(e.getMessage());
			e.printStackTrace();
		}catch(Exception ex){
			Logger.log("2"+ex.getMessage());
		}
	}
}
