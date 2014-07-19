package mapreduce;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class Context {

	ArrayList< Comparable<?> > list;
	
	public Context(String outputPath) {//For mapper/reducerTask
		
	}

	public Context() { //For InitTask

	}

	public void write(String key, Integer value) {
		
	}

	public Comparable<?>[] getMapperOutputKeys(){
		return null;
	}

	public long getMapperOutputSize() {
		// TODO Auto-generated method stub
		return 0;
	}
}
