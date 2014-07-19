package testfiles;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import commons.Logger;

public class TestProcessLauncherClient {
	public static void main(String args[]) throws IOException{
		int i = 0;
		
		File file = new File("C:/Users/Amey/workspace/example3/newFile.txt");
		// block this process
		Scanner sc = new Scanner(System.in);
		sc.nextLine();
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		byte[] byteArray = new byte[1024];
		while (i<10) {
			
			out.write((i + "").getBytes());
			if(i%10==0)
				out.write("\n".getBytes());
			i++;
		}

		out.close();
		System.exit(0);
	}
}
