package mapreduce;

import java.io.IOException;

public interface Mapper<InputKey, InputValue, IntermediateKey, IntermediateValue> {

	public void map(InputKey key, InputValue value, Context context)throws IOException, InterruptedException;
}
