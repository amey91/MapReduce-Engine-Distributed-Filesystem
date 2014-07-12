package datanode;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ConsoleThread extends Thread{
	/*
	 *  TODO implement:
	 *  ls, rm, mkdir, start, stop job, monitor
	 */
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String choice;
	String choices[];
	
	
	
	@Override
	public void run(){
		while(true){
			try{
				log("Please enter one of the following: (No spaces allowed in file name)"
						+ "\n localToHDFS <localFilePath> <HDFSFilePath>"
						+ "\n ls <folderPath>"
						+ "\n rm <fileName>"
						+ "\n mkdir <folderPath>"
						+ "\n startJob <jarFileName.jar>"
						+ "\n stopJob <jobId>"
						+ "\n monitor \n");
				choice = br.readLine();
				if(choice=="" || choice==null){
         			throw new Exception("Blank input not allowed.");
         		}
         		choices = choice.split(" ");
				choices[0] = choices[0].toLowerCase();
				
         		switch(choices[0]){
         		// TODO
         		case("localtohdfs"):
         			if(choices.length!=3){
         				log("got " + choices.length + " arguments. Expected 3 arguments");
         				throw new Exception("Wrong number of arguments!");
         			}
         			String localFilePath = choices[1];
         			String HDFSFilePath = choices[1];
         			
         			if(checkFileExists(localFilePath)){
         				throw new Exception(localFilePath + ": File does not exist");
         			}
         			log("Uploading file to HDFS");
         			new Thread(new LocalToHDFS(localFilePath,HDFSFilePath)).start();
         			break;
         			
         		case("ls"):
         			if(choices.length!=2){
         				log("got " + choices.length + " arguments. Expected 2 argument.");
         				throw new Exception("Wrong number of arguments!");
         			}
         			break;
         		case("rm"):
         			if(choices.length!=2){
         				log("got " + choices.length + " arguments. Expected 2 arguments");
         				throw new Exception("Wrong number of arguments!");
         			}
         			break;
         		case("mkdir"):
         			if(choices.length!=2){
         				log("got " + choices.length + " arguments. Expected 2 arguments");
         				throw new Exception("Wrong number of arguments!");
         			}
         			 break;
         		case("startjob"):
         			if(choices.length!=2){
         				log("got " + choices.length + " arguments. Expected 2 arguments");
         				throw new Exception("Wrong number of arguments!");
         			}
        			 break;
         		case("stopjob"):
         			if(choices.length!=2){
         				log("got " + choices.length + " arguments. Expected 2 arguments");
         				throw new Exception("Wrong number of arguments!");
         			}
        			 break;
         		case("monitor"):
         			if(choices.length!=2){
         				log("got " + choices.length + " arguments. Expected 2 arguments");
         				throw new Exception("Wrong number of arguments!");
         			}
        			 break;
         		default:
         			break;
         		}
				
			} catch(Exception e){
				log(e.getMessage());
			}//end of try
		}//end of while
	}
	
	private void ls(String remoteFilePath) throws Exception{
		
	}
	
	private void rm(String DFSFilePath) throws Exception{
		
	}
	
	private void mkdir(String DFSFilePath) throws Exception{
		
	}
	
	private boolean checkFileExists(String localFilePath){
		// referred to http://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-java-on-windows
		File f = new File(localFilePath);
		if(f.exists() && !f.isDirectory()) 
			return true;
		return false;
	}
	
	
	private void log(String a){
		System.out.println(a);
	}
}
