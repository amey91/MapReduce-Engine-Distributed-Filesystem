package temperaturetest;
// cc MaxTemperatureMapper Mapper for maximum temperature example
// vv MaxTemperatureMapper
import java.io.IOException;

import mapreduce.Mapper;
import mapreduce.Context;

public class Mapper1
  implements Mapper<Integer, String, String, Integer> {

  private static final int MISSING = 9999;
  
  @Override
  public void map(Integer key, String value, Context context)
      throws IOException, InterruptedException {
    
	  context.write(value, 1);
  }
}
// ^^ MaxTemperatureMapper