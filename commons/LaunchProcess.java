package commons;
import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

import mapreduce.Mapper;
import communication.KeyListMessage;

/*  @referred: http://www.xyzws.com/Javafaq/how-to-run-external-programs-by-using-java-processbuilder-class/189
 *  Launches a separate JVM with the specified path
 *  @param0: localRootPath
 *  @param1: className inside any local folder
 *  @param2: outputFile inside local root folder
 *  @param3: (OPTIONAL) additional arguments from Application Programmer for the mapper
 *  
 *  e.g. java LaunchProcess 
 */
public class LaunchProcess extends Thread { 
	@Override
	public void run(){
		try{
			String[] command = {"java.exe","-cp", "./", "commons.TestProcessLauncher", "ksad asd asd asd"};
			// String[] command = {"java.exe","-cp", "./", "jobhandler.StartJob","testJobName","testrootpath","testclassName","args"};
			ProcessBuilder probuilder = new ProcessBuilder( command );
			//You can set up your work directory
			//probuilder.directory(new File("c:/Temp"));
			probuilder.directory(new File("C:/Users/Amey/workspace/example3/"));
			File log = new File("C:/Temp/newFile.txt");
			probuilder.redirectErrorStream(true);
			probuilder.redirectOutput(Redirect.appendTo(log));
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
			int exitValue = process.waitFor();
			System.out.println("\n\nExit Value is " + exitValue);
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
