package temperaturetest;
// cc MaxTemperatureMapper Mapper for maximum temperature example
// vv MaxTemperatureMapper

// from https://www.youtube.com/watch?v=608lAGNUXs0
// https://github.com/tomwhite/hadoop-book/tree/master/ch02
import java.io.IOException;

import mapreduce.Context;
import mapreduce.Mapper;

public class MaxTemperatureMapper implements Mapper<String, Integer> {

  private static final int MISSING = 9999;
  
  @Override
  public void map(Long key, String value, Context<String, Integer> context)
      throws IOException, InterruptedException {
    
	  
    String line = value.toString();
    String year = line.substring(15, 19);
    // split record
    int airTemperature;
    if (line.charAt(87) == '+') { // parseInt doesn't like leading plus signs
      airTemperature = Integer.parseInt(line.substring(88, 92));
    } else {
      airTemperature = Integer.parseInt(line.substring(87, 92));
    }
    String quality = line.substring(92, 93);
	// write intermediate results to context
    if (airTemperature != MISSING && quality.matches("[01459]")) {
      context.write(new String(year), new Integer(airTemperature));
    }
  }
}
// ^^ MaxTemperatureMapper