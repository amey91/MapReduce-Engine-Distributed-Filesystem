package commons;


import java.awt.List;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import filesystem.Directory;

public class TestClient {
  public static void main(String[] argv) throws Exception {
    /*Socket sock = new Socket("127.0.0.1", 23456);    
    
    byte[] mybytearray = new byte[1024];
    InputStream is = sock.getInputStream();
    FileOutputStream fos = new FileOutputStream("C:\\Users\\Amey\\workspace\\example3\\commons\\s.pdf");
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    
    int bytesRead = is.read(mybytearray, 0, mybytearray.length);;
    int i =0;
    while(bytesRead>0 && !sock.isClosed()){
        bos.write(mybytearray, 0, bytesRead);
    	bytesRead = is.read(mybytearray, 0, mybytearray.length);
        System.out.println(" "+bytesRead);
        if(i++==10)
        	break;
	}
    bos.close();
    sock.close();
*/
	  Logger.log("HELOOEOELEOELEOE from test client");
	  //File ab = new File();
	  FileOutputStream fos = new FileOutputStream("C:/Temp/ab.txt");
	  byte[] arr = new byte[4];
	  arr[0] = 'a'; arr[1]='b'; arr[2]='c'; arr[3] =0;
	  fos.write(arr, 0, 4);
	  fos.close();
	  //Thread.sleep(3000);
    ArrayList<String> classNames=new ArrayList<String>();
    ZipInputStream zip=new ZipInputStream(new FileInputStream("C:/exp.jar"));
    for(ZipEntry entry=zip.getNextEntry();entry!=null;entry=zip.getNextEntry())
        if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
            // This ZipEntry represents a class. Now, what class does it represent?
            StringBuilder className=new StringBuilder();
            for(String part : entry.getName().split("/")) {
                if(className.length() != 0)
                    className.append(".");
                className.append(part);
                if(part.endsWith(".class"))
                    className.setLength(className.length()-".class".length());
            }
            classNames.add(className.toString());
        }
    zip.close();
   JarClassLoader jcl =  new JarClassLoader("C:/exp.jar");
   Class<?> c = jcl.loadClass(classNames.get(3)); 
   Directory d = (Directory) c.newInstance();
   d.getClass();
  }
}

   