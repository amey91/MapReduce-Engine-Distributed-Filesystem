package commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;

public class Test2 {
	public static void main(String ar[]) throws IOException, InterruptedException{
		
		   String[] command = {"java", "C:/Users/Amey/workspace/example3/commons/TestClient"};
	        ProcessBuilder probuilder = new ProcessBuilder( "java"," commons/TestClient" );
	        ProcessBuilder.Redirect.to(new File("C:/Temp/newFile.txt"));
	        
	        //You can set up your work directory
	        //probuilder.directory(new File("C:\\Temp\\"));
	        
	        Process process = probuilder.start();
	        
	        //Read out dir output
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        System.out.printf("Output of running %s is:\n",
	                Arrays.toString(command));
	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }
	        
	        //Wait to get exit value
	        try {
	            int exitValue = process.waitFor();
	            System.out.println("\n\nExit Value is " + exitValue);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		
	}
}
