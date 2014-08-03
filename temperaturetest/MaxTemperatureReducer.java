package temperaturetest;
// cc MaxTemperatureReducer Reducer for maximum temperature example
// vv MaxTemperatureReducer

// https://github.com/tomwhite/hadoop-book/tree/master/ch02
	
	
import java.io.IOException;

import mapreduce.Context;
import mapreduce.Reducer;

public class MaxTemperatureReducer
  implements Reducer<String, Integer, String, Integer> {
  
  @Override
  public void reduce(String key, Iterable<Integer> values, Context<String, Integer> context)
      throws IOException, InterruptedException {
    
    int maxValue = Integer.MIN_VALUE;
    for (Integer value : values) {
      maxValue = Math.max(maxValue, value);
    }
    // this is essential
    context.write(key, new Integer(maxValue));
  }
}
// ^^ MaxTemperatureReducer