package mapreduce;

import java.io.Serializable;

public class KeyValuePair<Key extends Comparable<Key>, Value > implements Serializable, 
					Comparable<KeyValuePair<Key, Value >>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2869273211960451820L;


	public Key key;
	public Value value;
	KeyValuePair(Key key, Value value){
		this.key = key;
		this.value = value;
	}
	@Override
	public int compareTo(KeyValuePair<Key, Value> other) {
		return key.compareTo(other.key);
	}
}