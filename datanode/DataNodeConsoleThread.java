package datanode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;

import namenode.InvalidDataNodeException;
import commons.Logger;
import filesystem.FileSystemException;

public class DataNodeConsoleThread extends Thread{
	/*
	 *  TODO implement:
	 *  ls, rm, mkdir, start, stop job, monitor
	 */
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String choice;
	String choices[];
	
	@Override
	public void run(){
		Logger.log("Run <help> for available commands");
		while(true){
			try{

				while(true){
					System.out.print(">");
					choice = br.readLine();
					if(choice!=null && !choice.trim().equals("") )
						break;
				}
				
         		choices = choice.split(" ");
				choices[0] = choices[0].toLowerCase();
				
         		switch(choices[0]){
         		// TODO
         		case("localtohdfs"):
         			if(choices.length!=3){
         				
         				throw new IOException("localToHDFS <localFilePath> <HDFSFilePath>");
         			}
         			String localFilePath = choices[1];
         			String HDFSFilePath = choices[2];
         			
         			if(!checkFileExists(localFilePath)){
         				throw new IOException(localFilePath + ": File does not exist");
         			}
         			log("Uploading file to HDFS");
         			new Thread(new LocalToHDFS(localFilePath,HDFSFilePath)).start();
         			break;
         			
         		case("hdfstolocal"):
         			if(choices.length!=3){
         				throw new IOException("HDFSToLocal <localFilePath> <HDFSFilePath>");
         			}

     				localFilePath = choices[1];
     				HDFSFilePath = choices[2];
         			
         			log("Uploading file to HDFS");
         			new Thread(new HDFSToLocal(localFilePath,HDFSFilePath)).start();
         			break;
         			
         		case("ls"):
         			if(choices.length > 2){
         				throw new IOException("ls <folderPath>");
         			}
         			if(choices.length == 1)
         				ls("/");
         			else
         				ls(choices[1]);
         			break;
         		case("rm"):
         			if(choices.length!=2){
         				throw new IOException("rm <fileName>");
         			}
         			rm(choices[1]);
         			break;
         		case("mkdir"):
         			if(choices.length!=2){
         				throw new IOException("mkdir <folderPath>!");
         			}
         			mkdir(choices[1]);
         			 break;
         		case("startjob"):
         			if(choices.length!=2){
         				throw new IOException("startJob <jarFileName.jar>");
         			}
         			// TODO
         		case("stopjob"):
         			if(choices.length!=2){
         				throw new IOException("stopJob <jobId>");
         			}
         			// TODO
        			 break;
         		case("stophb"):{
         			if(HeartbeatThread.stopHB == true)	
         				HeartbeatThread.stopHB = false;
         			else
         				HeartbeatThread.stopHB = true;
         			}
         			break;
         		case("monitor"):
         			if(choices.length!=2){
         				throw new IOException("monitor <job_id>");
         			}
         			// TODO
        			 break;
         		case("key"):
         			
         				log("my key: " + DataNode.key);

         			// TODO
        			 break;
         		case("help"):
         			log(	  "\n=================================================================="
    						+ "\nPlease enter one of the following: (No spaces allowed in file name)"
    						+ "\n localToHDFS <localFilePath> <HDFSFilePath>"
    						+ "\n HDFSToLocal <localFilePath> <HDFSFilePath>"
    						+ "\n ls <folderPath>"
    						+ "\n rm <fileName>"
    						+ "\n mkdir <folderPath>"
    						+ "\n startJob <jarFileName.jar>"
    						+ "\n stopJob <jobId>"
    						+ "\n monitor"
    						+ "\n key"
    						+ "\nstophb \n");
         			break;
         		default:
         			throw new IOException("Wrong input! Enter <help> for info");
         		}
				
			} catch(InvalidDataNodeException e){
				DataNode.reset();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				log(e.getMessage());
			}
		}//end of while
	}
	
	private void ls(String remoteFilePath) throws InvalidDataNodeException{	
		
		try {
			ArrayList<String> list = DataNode.nameNode.ls(DataNode.key,remoteFilePath);

			for(String s: list)
				Logger.log(s);
			
		} catch (RemoteException | FileSystemException e) {
			// TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		}
	}

	private void rm(String DFSFilePath) throws InvalidDataNodeException{

		try {
			DataNode.nameNode.rm(DataNode.key,DFSFilePath);
		} catch (RemoteException|FileSystemException e) {
			// TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		} 

	}
	
	private void mkdir(String DFSFilePath) throws InvalidDataNodeException {
		try {
			DataNode.nameNode.mkdir(DataNode.key,DFSFilePath);
		} catch (RemoteException | FileSystemException e) {
			// TODO delete
			Logger.log(e.getMessage());
			e.printStackTrace();
		}
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
