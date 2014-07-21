package mapreduce;

import java.io.IOException;
import java.io.Serializable;

public interface Mapper<Key extends Comparable<Key>, Value> extends Serializable {

	public void map(Long key, String value, Context<Key, Value> context)throws IOException, InterruptedException;
}