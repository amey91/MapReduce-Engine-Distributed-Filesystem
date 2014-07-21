package taskrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import commons.Logger;

import jarmanager.JarLoader;
import mapreduce.Context;
import mapreduce.Mapper;
import mapreduce.Reducer;

public class RunReducer {

	Class<Reducer> reducerClass;
	String[] localInputPaths;
	String outputLocalPath;
	int dataNodeListeningPort;
	Thread heartBeatThread;
	public double percent;
	
	public RunReducer(String jarFileLocalPath, String reducerClassName,
			String[] localInputPaths, String outputLocalPath) throws Exception {

		this.reducerClass = (Class<Reducer>) JarLoader.getClassFromJar(jarFileLocalPath, reducerClassName);
		this.localInputPaths = localInputPaths;
		this.outputLocalPath = outputLocalPath;
		this.heartBeatThread = new Thread(new TaskRunnerHeartBeatThread(this, dataNodeListeningPort));
		this.percent = 0;
	}

	public void Run() {

		try {
			Reducer r = reducerClass.newInstance();		
			

			Context context = new Context(outputLocalPath);

			//for(String inputPath: localInputPaths){
				//r.reduce(key, values, context);
			
		} catch (InstantiationException|IllegalAccessException|Exception e) {
			// TODO Auto-generated catch block
			Logger.log(e.getMessage());
			e.printStackTrace();
		}catch(Exception ex){
			Logger.log("2"+ex.getMessage());
		}

	}	
}
