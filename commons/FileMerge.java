package commons;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileMerge {
	
	//http://www.programcreek.com/2012/09/merge-files-in-java/
	public static void mergeFiles(String[] files, String mergedFile) throws IOException {
		 
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(mergedFile));
		
 
		for (String f : files) {
			Logger.log("merging: " + f);
			FileInputStream fis;
			
			fis = new FileInputStream(f);
			BufferedInputStream in = new BufferedInputStream(fis);
 
			byte[] byteArray = new byte[1024];
			while (true) {
				int bytesRead = in.read(byteArray, 0, byteArray.length);
				if(bytesRead <= 0)
					break;
				out.write(byteArray, 0, bytesRead);
			}
 
			in.close();
			
			(new File(f)).delete();
		}
 
		out.close();
	}
}
