package mapreduce;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class Context {

	ArrayList< Comparable<?> > list = new ArrayList< Comparable<?> >();
	int size = 0;
	String outputPath;
	public Context(String outputPath) {//For mapper/reducerTask
		this.outputPath = outputPath;
	}

	public Context() { //For InitTask

	}
 
	public void write(Comparable<?> key, Object value) {
		list.add(key);
	}

	public Comparable<?>[] getMapperOutputKeys(){
		return list.toArray(new Comparable<?>[list.size()]);
	}

	public long getMapperOutputSize() {
		// TODO get size from list
		return 1024;
	}
	public void dumpToFile(){
		//TODO dump the list to outputPath
	}
}
