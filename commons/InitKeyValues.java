package commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

import mapreduce.Mapper;

import communication.KeyListMessage;

public class InitKeyValues {
	public static KeyListMessage runInitMapper(Class<Mapper> mapper,
			String blockName, Boolean initTask) {
		try{

			String[] command = {"java.exe","-cp", "./", mapper.getName()};
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

			int exitCode = process.waitFor();
			if(exitCode <0)
				Logger.errLog("\n\nExit Value is " + exitCode);
			else if(exitCode>=0)
				Logger.log("Process Completed Successfully with exit code "+ exitCode);

		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			Logger.errLog("Local Error while launching Process.");
			e.printStackTrace();
		}
		// TODO change return null
		return null;
	}
}
