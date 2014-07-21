package conf;


//contains some constants in my project.
// most are configurable (with restrictions on REPLICATION_FACTOR)
public class Constants {
	// NOTE: the replication factor is assumed to be 3 at ALL TIMES. 
	// This system is not designed to work with replication factors other than 3
	public static final int REPLICATION_FACTOR = 3;
	
	// set minimum block size to 1 MB
	public static final long MIN_BLOCK_SIZE = (long) (1*Math.pow(2, 20));
	
	// frame size for communication
	public static final int FRAME_SIZE = 1024*1024;
	
	//maximum size that a reducer is allowed to handle
	public static final long MAX_REDUCER_SIZE = (long) (1*Math.pow(2, 20)); //Math.pow(2, 20)==1Mb
	//TODO add timeouts
	
	//TODO if(successArray[i]<=-5)
}
