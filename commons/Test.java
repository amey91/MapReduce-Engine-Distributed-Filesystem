package commons;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
		Runtime runtime =Runtime.getRuntime();
		Runtime.getRuntime().exec("java C:/Users/Amey/workspace/example3/commons/TestClient");
		Logger.log(a);
		Logger.log(jobSocket.getInetAddress().getHostAddress());
		Logger.log(5*Math.pow(2, 20)+"");
		
		Logger.log(System.currentTimeMillis()+"");
		Thread.sleep(100);
		Logger.log(System.currentTimeMillis()+"");	
		
		String lll = "/bib.txt";
		String[] llll = lll.trim().split("/");
		Logger.log(llll[0].length() +"" ) ;
		System.exit(0);
		
		File f = new File("C:/Temp/testpro.txt");
		
		try{
			FileOutputStream fos = new FileOutputStream("C:/Temp/testprocessbuilder.txt");
			fos.close();
		}catch(FileNotFoundException e){
			Logger.log("Invalid output file location: C:/Temp/testprocessbuilder.txt");
			return;
		}
		
		if(!f.canWrite()){
			Logger.log("Invalid output file location");
			//return;
		}
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("C:/Temp/testprocessbuilder.txt"));

		FileOutputStream fout = new FileOutputStream(f);
		byte[] writeb = a.getBytes();
		fout.write(writeb);
		fout.close();
		out.write(writeb);
		out.close();
	}
}
