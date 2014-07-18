package mapreduce;

import java.io.IOException;
import java.io.Serializable;

public interface Mapper<InputKey, InputValue, IntermediateKey, IntermediateValue> extends Serializable {

	public void map(InputKey key, InputValue value, Context context)throws IOException, InterruptedException;
}