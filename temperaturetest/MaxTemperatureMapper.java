package temperaturetest;
// cc MaxTemperatureMapper Mapper for maximum temperature example
// vv MaxTemperatureMapper
import java.io.IOException;

import mapreduce.Mapper;
import mapreduce.Context;

public class MaxTemperatureMapper
  implements Mapper<Integer, String, String, Integer> {

  private static final int MISSING = 9999;
  
  @Override
  public void map(Integer key, String value, Context context)
      throws IOException, InterruptedException {
    
    String line = value.toString();
    String year = line.substring(15, 19);
    int airTemperature;
    if (line.charAt(87) == '+') { // parseInt doesn't like leading plus signs
      airTemperature = Integer.parseInt(line.substring(88, 92));
    } else {
      airTemperature = Integer.parseInt(line.substring(87, 92));
    }
    String quality = line.substring(92, 93);
    if (airTemperature != MISSING && quality.matches("[01459]")) {
      context.write(new String(year), new Integer(airTemperature));
    }
  }
}
// ^^ MaxTemperatureMapper