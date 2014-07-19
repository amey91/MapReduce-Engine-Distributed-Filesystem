package commons;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestProcessLauncher {
	public static void main(String args[]) throws IOException{
		int i = 0;
		Logger.log(args[0]+  "");
		File file = new File("C:/Users/Amey/workspace/example3/newFile.txt");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		byte[] byteArray = new byte[1024];
		while (i<1) {
			
			out.write((args[0] + "").getBytes());
			if(i%10==0)
				out.write("\n".getBytes());
			i++;
		}

		out.close();
		System.exit(0);
	}
}
