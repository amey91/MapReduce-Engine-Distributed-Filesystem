package temperaturetest;
// cc MaxTemperatureReducer Reducer for maximum temperature example
// vv MaxTemperatureReducer
import java.io.IOException;

import mapreduce.Context;
import mapreduce.Reducer;

public class Reducer1
  implements Reducer<String, Integer, String, Integer> {
  
  @Override
  public void reduce(String key, Iterable<Integer> values, Context<String, Integer> context)
      throws IOException, InterruptedException {
    
    int total = 0;
    for (Integer value : values) {
      total += value;
    }
    context.write(key, new Integer(total));
  }
}
// ^^ MaxTemperatureReducer