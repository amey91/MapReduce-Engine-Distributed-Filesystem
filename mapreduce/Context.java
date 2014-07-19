package mapreduce;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class Context {

	ArrayList< Comparable<?> > list;
	int size = 0;
	public Context(String outputPath) {//For mapper/reducerTask
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
		// TODO Auto-generated method stub
		return 1024;
	}
}
