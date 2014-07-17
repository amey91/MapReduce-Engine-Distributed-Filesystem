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

class ccc{
	public String s;
};

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
	  //File ab = new File();
	  FileOutputStream fos = new FileOutputStream("C:/Temp/newFile.txt");
	  byte[] arr = new byte[4];
	  arr[0] = 'a'; arr[1]='b'; arr[2]='c'; arr[3] =0;
	  fos.write(arr, 0, 4);
	  
	  //Thread.sleep(3000);
    ArrayList<String> classNames=new ArrayList<String>();
    ZipInputStream zip=new ZipInputStream(new FileInputStream("C:/Temp/test.jar"));
    
    for(ZipEntry entry=zip.getNextEntry();entry!=null;entry=zip.getNextEntry())
        if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
            // This ZipEntry represents a class. Now, what class does it represent?
            StringBuilder className=new StringBuilder();
            for(String part : entry.getName().split("/")) {
                    className.append(".");
                className.append(part);
   //             if(part.endsWith(".class"))
 //                   className.setLength(className.length()-".class".length());
            }
            classNames.add(className.toString());
        }
    zip.close();
   JarClassLoader jcl =  new JarClassLoader("C:/Temp/test.jar");
   //Class<?> c = jcl.loadClass(classNames.get(3)); 
 //  Directory d = (Directory) c.newInstance();
   //d.getClass();
   String classes = "";
   for(String p: classNames)
	   classes += p;
   byte[] b = classes.getBytes();
   fos.write(b, 0, b.length);
   fos.close();
  }
}

   