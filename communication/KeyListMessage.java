package communication;

// type of message
//Message containing range of keys for map-reduce job partitioning
public class KeyListMessage extends Message{

	public Comparable<?>[] keyList;

	private static final long serialVersionUID = 1073535764855374763L;
	// this is the size of the key-value pair with respect to the orignal input row size
	public long outputSizeEstimate;
	public KeyListMessage(String type, Comparable<?>[] a, long outputSizeEstimate) {
		super(type);
		this.keyList = a;
		this.outputSizeEstimate = outputSizeEstimate;
	}
}
