package commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class Test {
	public static void main(String ar[]) throws IOException, InterruptedException{
		ServerSocket jobSocket = new ServerSocket(0);
		String a = InetAddress.getLocalHost().toString();
		a = a.substring(a.indexOf('/')+1);
		Logger.log(a);
		Logger.log(jobSocket.getInetAddress().getHostAddress());
		Logger.log(5*Math.pow(2, 20)+"");
		
		Logger.log(System.currentTimeMillis()+"");
		Thread.sleep(100);
		Logger.log(System.currentTimeMillis()+"");	
		
		String lll = "/bib.txt";
		String[] llll = lll.trim().split("/");
		Logger.log(llll[0].length() +"" ) ;
	}
}
