package temperaturetest;
// cc MaxTemperatureReducer Reducer for maximum temperature example
// vv MaxTemperatureReducer
import java.io.IOException;

import mapreduce.Reducer;
import mapreduce.Context;

public class MaxTemperatureReducer
  implements Reducer<String, Integer, String, Integer> {
  
  @Override
  public void reduce(String key, Iterable<Integer> values, Context context)
      throws IOException, InterruptedException {
    
    int maxValue = Integer.MIN_VALUE;
    for (Integer value : values) {
      maxValue = Math.max(maxValue, value);
    }
    context.write(key, new Integer(maxValue));
  }
}
// ^^ MaxTemperatureReducer