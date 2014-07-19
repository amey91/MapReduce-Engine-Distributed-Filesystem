package taskrunner;

import jarmanager.JarLoader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import mapreduce.Context;
import mapreduce.Mapper;
import commons.Logger;
import communication.Communicator;
import communication.KeyListMessage;

public class RunInit {
	Class<Mapper> mapperClass;
	Boolean isInitTask;
	String blockLocalPath;
	int dataNodeListeningPort;
	
	public RunInit(String jarFileLocalPath, String mapperClassName, String blockLocalPath, int dataNodeListeningPort){

		this.mapperClass = (Class<Mapper>) JarLoader.getClassFromJar(jarFileLocalPath, mapperClassName);
		this.blockLocalPath = blockLocalPath;
		this.dataNodeListeningPort = dataNodeListeningPort;
	}
	
	
	public void Run(Socket incomingSocket){
		
		try {
			Mapper m = mapperClass.newInstance();
			Logger.log("dataNodeListeningPort = " + dataNodeListeningPort);
	
			Socket heartBeatSocket = new Socket("127.0.0.1", dataNodeListeningPort);

			RandomAccessFile file = new RandomAccessFile(blockLocalPath, "r");
			long fileLength = file.length();

			Context context = new Context();
			long readRequired = (long)(12*Math.log((double)fileLength)/Math.log(2.0));
			
			long totalRead = 0;
			while(totalRead < readRequired){
				
				long location = (long) (Math.random()*fileLength);
				file.seek(location);
				//while(file.read()!='\n');
				
				String s = file.readLine();

				Logger.log(s);
				m.map(0, s, context);
				totalRead += s.length();
			}
			long estimate = (context.getMapperOutputSize()*fileLength)/readRequired;

			KeyListMessage message = new KeyListMessage("InitKeyListMessage", context.getMapperOutputKeys(), estimate);
			Communicator.sendMessage(incomingSocket, message);
			file.close();
		
		} catch (InstantiationException|IllegalAccessException|IOException|InterruptedException e) {
			// TODO Auto-generated catch block
			Logger.log(e.getMessage());
			e.printStackTrace();
		}catch(Exception ex){
			Logger.log("2"+ex.getMessage());
		}
	}
}
