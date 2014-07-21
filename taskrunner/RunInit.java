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

public class RunInit<Key extends Comparable<Key>, Value> {
	Class<Mapper<Key, Value>> mapperClass;
	Boolean isInitTask;
	String blockLocalPath;

	public RunInit(String jarFileLocalPath, String mapperClassName, String blockLocalPath){
		try{
			this.mapperClass = (Class<Mapper<Key, Value> >) JarLoader.getClassFromJar(jarFileLocalPath, mapperClassName);
		} catch (Exception e) {
			Logger.log("Eror while loading Jar file:");
			e.printStackTrace();
		}
		this.blockLocalPath = blockLocalPath;
	}


	public void Run(Socket incomingSocket){

		try {
			Mapper<Key, Value> m = mapperClass.newInstance();

			RandomAccessFile file = new RandomAccessFile(blockLocalPath, "r");
			long fileLength = file.length();

			Context<Key, Value> context = new Context<Key, Value>();
			long readRequired = (long)(12*Math.log((double)fileLength)/Math.log(2.0));

			long totalRead = 0;
			while(totalRead < readRequired){

				long location = (long) (Math.random()*fileLength);
				file.seek(location);
				//while(file.read()!='\n');

				String s = file.readLine();

				Logger.log(s);
				m.map((long)0, s, context);
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
