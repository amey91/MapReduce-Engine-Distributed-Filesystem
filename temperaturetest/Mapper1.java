package temperaturetest;
// cc MaxTemperatureMapper Mapper for maximum temperature example
// vv MaxTemperatureMapper
import java.io.IOException;

import mapreduce.Context;
import mapreduce.Mapper;

public class Mapper1
  implements Mapper<String, Integer> {

  /**
	 * 
	 */
	private static final long serialVersionUID = -3928875545753337838L;

  
  @Override
  public void map(Long key, String value, Context<String, Integer> context)
      throws IOException, InterruptedException {
    
	  String[] split = value.split(" ");
	  for(String s: split)
		  context.write(s, 1);
  }
}
// ^^ MaxTemperatureMapper