package taskrunner;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.Socket;

import communication.Communicator;
import communication.HeartbeatMessage;
import communication.KeyListMessage;
import jarmanager.JarLoader;
import mapreduce.Context;
import mapreduce.Mapper;

public class RunMapper {
	
	Class<Mapper> mapperClass;
	Boolean isInitTask;
	String blockLocalPath;
	String outputPath;
	int dataNodeListeningPort;
	
	public RunMapper(String jarFileLocalPath, String mapperClassName, String blockLocalPath, 
			String outputPath, Boolean isInitTask, int dataNodeListeningPort){

		this.mapperClass = (Class<Mapper>) JarLoader.getClassFromJar(jarFileLocalPath, mapperClassName);
		this.blockLocalPath = blockLocalPath;
		this.outputPath = outputPath;
		this.isInitTask = isInitTask;
		
		
	}
	
	public void Run(){
		
		
		try {
			Mapper m = mapperClass.newInstance();Socket heartBeatSocket = new Socket("127.0.0.1", dataNodeListeningPort);

			RandomAccessFile file = new RandomAccessFile(blockLocalPath, "r");
			long fileLength = file.length();
			if(isInitTask){

				Context context = new Context();
				long readRequired = (long)(12*Math.log((double)fileLength)/Math.log(2.0));
				
				long totalRead = 0;
				while(totalRead < readRequired){
					
					long location = (long) (Math.random()*fileLength);
					file.seek(location);
					while(file.read()!='\n');
					String s = file.readLine();
					m.map(0, s, context);
				}
				long estimate = (context.getMapperOutputSize()*fileLength)/readRequired;

				KeyListMessage message = new KeyListMessage("InitKeyListMessage", context.getMapperOutputKeys(), estimate);
				Communicator.sendMessage(heartBeatSocket, message);
				file.close();
			} 
			else{
				file.close();
				InputStream    fis = new FileInputStream(blockLocalPath);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				

				Context context = new Context(outputPath);
				
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
				    	HeartbeatMessage message = new HeartbeatMessage("Heartbeat", percent, false);
				    	Communicator.sendMessage(heartBeatSocket, message);
				    }
				    
				}

		    	HeartbeatMessage message = new HeartbeatMessage("Heartbeat", 100, true);
		    	Communicator.sendMessage(heartBeatSocket, message);
		    	
				br.close();
				br = null;
				fis = null;	
			}
		} catch (InstantiationException|IllegalAccessException|IOException|InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
