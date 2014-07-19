package communication;

public class MapperTaskMessage extends Message {

	
	public String jarFileLocalPath;
	public String mapperClassName;
	public String blockLocalPath;
	public String outputPath;

	public MapperTaskMessage(String type, String jarFileLocalPath, String mapperClassName, 
			String blockLocalPath, String outputPath) {
		super(type);
		this.jarFileLocalPath = jarFileLocalPath;
		this.mapperClassName = mapperClassName;
		this.blockLocalPath = blockLocalPath;
		this.outputPath = outputPath;
		// TODO Auto-generated constructor stub
	}

}
