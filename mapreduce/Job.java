package mapreduce;

import java.io.Serializable;

public class Job implements Serializable{

		/**
	 * 
	 */
	private static final long serialVersionUID = -1588390432253207345L;
		public String mapperClassName;
		public String reducerClassName;
		public String inputPath;
		public String outputPath;
		public String jobName;
		public String jarFile;
		public String datanodeKey; 
		
		
	public Job(String datanodeKey, String jobName, String jarPath, String mapperName, String reducerName, String inputPath, String outputPath) {
		this.datanodeKey = datanodeKey;
		this.jobName = jobName;
		this.mapperClassName = mapperName;
		this.reducerClassName = reducerName;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.jarFile = jarPath;
	}
	
/*
	public int waitForCompletion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setInputPath(Path path) {
		// TODO Auto-generated method stub
		
	}

	public void setOutputPath(Path path) {
		// TODO Auto-generated method stub
		
	}
	
	public void setMapperClass(Class<Mapper<?,?,?,?> > mapperClass) {
		
	}
	
	public void setReducerClass(Class<Reducer<?,?,?,?> > reducerClass) {
		// TODO Auto-generated method stub
		//Reducer r = reducerClass.newInstance();
		//r.reduce("", "", new Context());
		
		
	}

	public void setOutputKeyClass(Class<?> outputKey) {
		// TODO Auto-generated method stub
		
	}

	public void setOutputValueClass(Class<?> class1) {
		// TODO Auto-generated method stub
	}*/
}
