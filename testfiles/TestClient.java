package testfiles;


import jarmanager.JarClassLoader;
import jarmanager.JarLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import mapreduce.Context;
import mapreduce.Mapper;

import commons.Logger;

// This class is used to load a class
public class TestClient {
	public static void main(String[] argv) throws Exception {
		
		Class<Mapper> mhj =(Class<Mapper>) JarLoader.getClassFromJar("C:\\Temp\\1/0.jar", "temperaturetest.Mapper1");
		Logger.log("SDssd");
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
		//Thread.sleep(3000);
		
		String localFileLocation = "C:/Users/Amey/workspace/example3/exp.jar";
		String className = "temperaturetest.MaxTemperatureMapper123";
		Class<?> c = null;
		try{
			ArrayList<String> classNames=new ArrayList<String>();
			//ZipInputStream zip=new ZipInputStream(new FileInputStream("E:/example/example3/exp.jar"));
			ZipInputStream zip=new ZipInputStream(new FileInputStream(localFileLocation));

			for(ZipEntry entry=zip.getNextEntry();entry!=null;entry=zip.getNextEntry())
				if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
					// This ZipEntry represents a class. Now, what class does it represent?
					StringBuilder classNameString=new StringBuilder();
					for(String part : entry.getName().split("/")) {
						classNameString.append(part);
						classNameString.append(".");
						if(part.endsWith(".class"))
							classNameString.setLength(classNameString.length()-"..class".length()); //extra dot for the one which we inserted 
					}
					classNames.add(className.toString());
				}
			zip.close();


			JarClassLoader jcl =  new JarClassLoader(localFileLocation);
			Logger.log(classNames.get(0));
			c = jcl.loadClass(className);//classNames.get(0));

			Type[] t = c.getGenericInterfaces();

			Mapper<String, Integer> mp = (Mapper<String, Integer>)c.newInstance();
			mp.map((long) 3, "huj", new Context());

		}
		catch(Exception e){
			Logger.log("Error while reding from jar file...");
			e.printStackTrace();
		}

		//return c;
	}


	public static Class<?> sendClass() throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException{

		String location = "C:/Users/Amey/workspace/example3/exp.jar";
		ArrayList<String> classNames=new ArrayList<String>();
		ZipInputStream zip=new ZipInputStream(new FileInputStream(location));

		for(ZipEntry entry=zip.getNextEntry();entry!=null;entry=zip.getNextEntry())
			if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
				// This ZipEntry represents a class. Now, what class does it represent?
				StringBuilder className=new StringBuilder();
				for(String part : entry.getName().split("/")) {
					className.append(".");
					className.append(part);
					if(part.endsWith(".class"))
						className.setLength(className.length()-".class".length());
				}
				classNames.add(className.toString());
			}
		zip.close();


		JarClassLoader jcl =  new JarClassLoader(location);
		Logger.log(classNames.get(0));
		Class<?> c = jcl.loadClass("temperaturetest.MaxTemperatureMapper123");//classNames.get(0));
		Type[] t = c.getGenericInterfaces();

		Mapper<String, Integer> mp = (Mapper<String, Integer>)c.newInstance();
		mp.map((long) 3, "huj", new Context<String, Integer>());
		return c;
	}
}