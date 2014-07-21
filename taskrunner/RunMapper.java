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


// run a mapper task for the specified file block
public class RunMapper {
	
	Class<Mapper> mapperClass;
	Boolean isInitTask;
	String blockLocalPath;
	String outputLocalPath;
	Thread heartBeatThread;
	public double percent;
	
	public RunMapper(String jarFileLocalPath, String mapperClassName, String blockLocalPath, 
			String outputLocalPath, int dataNodeListeningPort) throws Exception{
		this.percent = 0;
		this.mapperClass = (Class<Mapper>) JarLoader.getClassFromJar(jarFileLocalPath, mapperClassName);
		this.blockLocalPath = blockLocalPath;
		this.outputLocalPath = outputLocalPath;
		this.heartBeatThread = new Thread(new TaskRunnerHeartBeatThread(this,dataNodeListeningPort));
	}
	
	public void Run(){
		
		try {
			Mapper m = mapperClass.newInstance();			

			File file = new File(blockLocalPath, "r");
			long fileLength = file.length();
			
			InputStream    fis = new FileInputStream(blockLocalPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			// start HB thread after file is initialized
			this.heartBeatThread.start();
			
			Context context = new Context(outputLocalPath);
			
			String line;
			int lineNumber = 0;			
			
			long totalRead = 0;
			while ((line = br.readLine()) != null) {
			    m.map(lineNumber, line, context);
			    lineNumber++;
			    totalRead += line.length();
				this.percent = (totalRead*1.0)/fileLength;
			}			
			context.dumpToFile();

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
