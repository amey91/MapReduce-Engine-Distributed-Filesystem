package mapreduce;

import java.nio.file.Path;

import temperaturetest.MaxTemperatureMapper;

public class Job {

		Mapper mapper;
		Reducer reducer;
		Path inputPath;
		Path outputPath;
		String jobName;
		String jobId;
		
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
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
	
	public void setMapperClass(Class<?> mapperClass) {
		
	}
	
	public void setReducerClass(Class<?> reducerClass) {
		// TODO Auto-generated method stub
		
	}

	public void setOutputKeyClass(Class<?> outputKey) {
		// TODO Auto-generated method stub
		
	}

	public void setOutputValueClass(Class<?> class1) {
		// TODO Auto-generated method stub
	}
}
