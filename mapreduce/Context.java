package mapreduce;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import commons.Logger;

public class Context<Key extends Comparable<Key>, Value > {

	ArrayList< KeyValuePair<Key, Value> > list = new ArrayList< KeyValuePair<Key, Value> >();
	int size = 0;
	String outputPath;
	public Context(String outputPath) {//For mapper/reducerTask
		this.outputPath = outputPath;
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
			
		FileOutputStream fos = new FileOutputStream(outputPath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(list);
		oos.close();
	}
}
