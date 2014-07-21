package jarmanager;

import commons.Logger;

//This class is used to load a class from a jar file
public class JarLoader {
	public static Class<?> getClassFromJar(String localFileLocation, String className) throws Exception {
		
		try{
			JarClassLoader jcl =  new JarClassLoader(localFileLocation);
			return jcl.loadClass(className);
		}
		catch(Exception e){
			Logger.log("Error while reding from jar file...");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		
	}
	/*

  public static Class<?> sendClass() throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException{

	  	String location = "C:/Users/Amey/workspace/example3/exp.jar";
	    ArrayList<String> classNames=new ArrayList<String>();
	    ZipInputStream zip=new ZipInputStream(new FileInputStream(location));

	    for(ZipEntry entry=zip.getNextEntry();entry!=null;entry=zip.getNextEntry())
	        if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
	            // This ZipEntry represents a class. Now, what class does it represent?
	            StringBuilder className=new StringBuilder();
	            for(String part : entry.getName().split("/")) {
	                className.append(part);
	                className.append(".");
	                if(part.endsWith(".class"))
	                    className.setLength(className.length()-".class".length());
	            }
	            classNames.add(className.toString());
	        }
	    zip.close();


	   JarClassLoader jcl =  new JarClassLoader(location);
	   Logger.log(classNames.get(0));
	   Class<?> c = jcl.loadClass(classNames.toString());//classNames.get(0));
	   Type[] t = c.getGenericInterfaces();

	   Mapper<Integer, String, String, Integer> mp = (Mapper<Integer, String, String, Integer>)c.newInstance();
	   mp.map(3, "huj", new Context());
	   return c;
  }*/
	
	
	
	//classNames.get(0));
	/*ArrayList<String> classNames=new ArrayList<String>();
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
	zip.close();*/
	//Logger.log(classNames.get(0));
}