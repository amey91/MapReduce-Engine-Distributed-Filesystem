package commons;


// simple console logging function for user io or debugging
public class Logger {

	public static void log(String a){
		System.out.println(a);
	}
	
	public static void errLog(String a){
		System.err.println(a);
	}
	
}
