package mapreduce;

import java.io.IOException;

public interface Reducer<Key1 extends Comparable<Key1>, Value1, Key2 extends Comparable<Key2>, Value2> {

	  public void reduce(Key1 key, Iterable<Value1> values, Context<Key2, Value2> context)throws IOException, InterruptedException;
}
