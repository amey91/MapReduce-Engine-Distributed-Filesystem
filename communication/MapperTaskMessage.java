package communication;

public class MapperTaskMessage extends Message {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5663891075722279082L;
	
	public String jarFileLocalPath;
	public String mapperClassName;
	public String blockLocalPath;
	public String outputLocalPath;
	Comparable<?>[] splits;

	public MapperTaskMessage(String jarFileLocalPath, String mapperClassName, 
			String blockLocalPath) {
		super("InitTask");
		this.jarFileLocalPath = jarFileLocalPath;
		this.mapperClassName = mapperClassName;
		this.blockLocalPath = blockLocalPath;
	}
	
	public MapperTaskMessage(String jarFileLocalPath, String mapperClassName, Comparable<?>[] splits,
			String blockLocalPath, String outputLocalPath) {
		super("MapperTask");
		this.jarFileLocalPath = jarFileLocalPath;
		this.mapperClassName = mapperClassName;
		this.blockLocalPath = blockLocalPath;
		this.outputLocalPath = outputLocalPath;
		this.splits = splits;
	}

}