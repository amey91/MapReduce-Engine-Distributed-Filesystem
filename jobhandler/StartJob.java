package jobhandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

// start a job at this datanode
public class StartJob {
	static Path rootPath ;
	static String classNameToRun;
	static String arguments;
	static String jobName;
	static String outputFileName;
	
	static int i = 0;

	
	public static void setVars(String jobName, String className, String rootPath,  String args){
		StartJob.jobName = jobName;
		StartJob.rootPath = Paths.get(rootPath);
		StartJob.classNameToRun = className;
		StartJob.arguments = args;
	}
	
	public static void main(String args[]) throws InterruptedException, IOException{
		int numArgs = args.length;
		if(numArgs < 3)
			System.out.println("StartJob Usage: startJob <jobName> <className> <rootPath> <outputFileName> <arguments(optional)>");
		
		
		for(int i = 3; i<numArgs; i++){
			
		}
		
		setVars(args[0],args[1],args[2],args[3]);
		
		
        //String[] command = {"java.exe","-cp", "./", "jobhandler.StartJob","testJobName","testrootpath","testclassName","args"};

        String[] command  = {"java.exe","-cp", "./", "commons.TestPBClient","asd"};
        ProcessBuilder probuilder = new ProcessBuilder( "java.exe","-cp", "./", "commons.TestPBClient","asd");
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
        try {
            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
	
}
