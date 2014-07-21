package communication;


// type of message 
public class ReducerTaskMessage extends Message {

	private static final long serialVersionUID = -5663891075722279082L;
	
	public String jarFileLocalPath;
	public String reducerClassName;
	public String[] localInputPaths;
	public String outputLocalPath;
	Comparable<?>[] splits;

	public ReducerTaskMessage(String jarFileLocalPath, String reducerClassName, 
			String[] localInputPaths, String outputLocalPath) {
		super("ReducerTask");
		this.jarFileLocalPath = jarFileLocalPath;
		this.reducerClassName = reducerClassName;
		this.localInputPaths = localInputPaths;
		this.outputLocalPath = outputLocalPath;
	}

}