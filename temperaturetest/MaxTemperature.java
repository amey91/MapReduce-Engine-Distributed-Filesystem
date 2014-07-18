package temperaturetest;

// cc MaxTemperature Application to find the maximum temperature in the weather dataset
// vv MaxTemperature


import mapreduce.Job;

import java.nio.file.Paths;

public class MaxTemperature {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: MaxTemperature <input path> <output path>");
      System.exit(-1);
    }
    /*
    Job job = new Job();
    //job.setJarByClass(MaxTemperature.class);
    job.setJobName("Max temperature");

    job.setInputPath(Paths.get(args[0]));
    job.setOutputPath(Paths.get(args[1]));
    
    job.setMapperClass(MaxTemperatureMapper.class);
    job.setReducerClass(MaxTemperatureReducer.class);

    job.setOutputKeyClass(String.class);
    job.setOutputValueClass(Integer.class);
    
    System.exit(job.waitForCompletion());
    
    Job job2 = new Job();
    job2.waitForCompletion();*/
  }
}
// ^^ MaxTemperature