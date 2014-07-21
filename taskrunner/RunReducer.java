package taskrunner;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

import jarmanager.JarLoader;
import mapreduce.Context;
import mapreduce.KeyValuePair;
import mapreduce.Reducer;
import commons.Logger;
import communication.KeyListMessage;

public class RunReducer<Key1 extends Comparable<Key1>, Value1, Key2 extends Comparable<Key2>, Value2> {

	Class<Reducer<Key1, Value1, Key2, Value2>> reducerClass;
	String[] localInputPaths;
	String outputLocalPath;
	int dataNodeListeningPort;
	Thread heartBeatThread;
	public double percent;
	
	public RunReducer(String jarFileLocalPath, String reducerClassName,
			String[] localInputPaths, String outputLocalPath) throws Exception {

		this.reducerClass = (Class<Reducer<Key1, Value1, Key2, Value2>>) JarLoader.getClassFromJar(jarFileLocalPath, reducerClassName);
		this.localInputPaths = localInputPaths;
		this.outputLocalPath = outputLocalPath;
		this.heartBeatThread = new Thread(new TaskRunnerHeartBeatThread(this, dataNodeListeningPort));
		this.percent = 0;
	}

	public void Run() {
		try {
			ArrayList<KeyValuePair<Key1, Value1>> inputList = new ArrayList<KeyValuePair<Key1, Value1>>();
			for(String path:localInputPaths){
				FileInputStream fis = new FileInputStream(path);
				ObjectInputStream ois = new ObjectInputStream(fis);
				ArrayList<KeyValuePair<Key1, Value1>> subList = (ArrayList<KeyValuePair<Key1, Value1>>) ois.readObject();
				ois.close();
				inputList.addAll(subList);
			}
			//TODO merge sort
			Collections.sort(inputList);
				

			Reducer<Key1, Value1, Key2, Value2> r = reducerClass.newInstance();

			Context<Key2, Value2> context = new Context<Key2, Value2>(outputLocalPath);
			Key1 previousKey = null;
			ArrayList<Value1> valueList = null;
			for(KeyValuePair<Key1, Value1> p: inputList){
				if(previousKey == null || previousKey!=p.key){
					if(valueList !=null)
						r.reduce(previousKey, valueList, context);
					
					valueList = new ArrayList<Value1>();
					valueList.add(p.value);
					previousKey = p.key;
						
				}
				else
					valueList.add(p.value);
			}
			if(valueList!=null && previousKey!=null)
				r.reduce(previousKey, valueList, context);
			
			context.dumpToFile();
		} catch (InstantiationException|IllegalAccessException e) {
			Logger.log(e.getMessage());
			e.printStackTrace();
		}catch(Exception ex){
			Logger.log("2"+ex.getMessage());
		}

	}	
}
