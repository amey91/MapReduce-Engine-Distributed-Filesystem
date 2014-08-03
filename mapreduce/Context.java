package mapreduce;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import commons.Logger;

public class Context<Key extends Comparable<Key>, Value > {

	ArrayList< KeyValuePair<Key, Value> > list = new ArrayList< KeyValuePair<Key, Value> >();
	int size = 0;
	String outputPath;

	Key[] splits;
	
	public Context(String outputPath) {//For reducerTask
		this.outputPath = outputPath;
		this.splits = null;
	}
	public Context(String outputPath, Key[] splits) {//For mapperTask
		Logger.log("mapper constructor:" + splits);
		this.outputPath = outputPath;
		this.splits = splits;
	}

	public Context() { //For InitTask
		outputPath = null;
	}
 
	public void write(Key key, Value value) {
		list.add(new KeyValuePair<Key, Value>(key, value));
	}

	public Comparable<?>[] getMapperOutputKeys(){
		return list.toArray(new Comparable<?>[list.size()]);
	}

	public long getMapperOutputSize() throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
	    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

	    objectOutputStream.writeObject(list);
	    objectOutputStream.flush();
	    objectOutputStream.close();

	    return byteOutputStream.toByteArray().length;
	}
	public void dumpToFile() throws IOException{
		if(outputPath == null){
			Logger.log("No output path provided to Context");
		}

		if(splits==null){
			PrintWriter writer = new PrintWriter(outputPath);
			for(KeyValuePair kvp:list)
				writer.println(kvp.key + ":" + kvp.value);
			writer.close();
		}
		else{
			Logger.log("dumping2");
			Collections.sort(list);
			ArrayList< KeyValuePair<Key, Value> > tempList = new ArrayList< KeyValuePair<Key, Value> >();
			Iterator<KeyValuePair<Key, Value>> iter = list.iterator();
			
			int splitLocation = 0;
			
			while(iter.hasNext()){
				KeyValuePair<Key, Value> kvp = iter.next();
				if(splitLocation<splits.length && kvp.key.compareTo((Key) (((KeyValuePair)splits[splitLocation]).key)  ) > 0 ){
					FileOutputStream fos = new FileOutputStream(outputPath + "_" + splitLocation);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(tempList);
					oos.close();
					
					while(splitLocation<splits.length && kvp.key.compareTo((Key) (((KeyValuePair)splits[splitLocation]).key) ) > 0 )
						splitLocation++;
					tempList = new ArrayList< KeyValuePair<Key, Value> >();
					tempList.add(kvp);
				} else
					tempList.add(kvp);
			}
			
			if(tempList.size() > 0 ){
				FileOutputStream fos = new FileOutputStream(outputPath + "_" + splitLocation);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(tempList);
				oos.close();
			}
		}
	}
}
