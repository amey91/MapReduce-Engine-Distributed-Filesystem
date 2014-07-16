package mapreduce;

import java.io.IOException;

public interface Reducer<IntermediateKey, IntermediateValue, OutputKey, OutputValue> {

	  public void reduce(IntermediateKey key, Iterable<IntermediateValue> values, Context context)throws IOException, InterruptedException;
}
