package namenode;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import commons.Logger;

public class NameNodeConsoleThread extends Thread{
	/*
	 *  TODO implement:
	 *  ls, rm, mkdir, start, stop job, monitor
	 */
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String choice;
	
	@Override
	public void run(){
		while(true){
			try{
				Logger.log("Please enter\"5\" to view a map of registered DataNodes"
						+ "\n Please enter \"6\" to view running jobs");
				choice = br.readLine();
				if(choice=="" || choice==null){
         			throw new Exception("Blank input not allowed.");
         		}
         		switch(choice){
	         		case("5"):
	         			NameNode.instance.displayDataNodes();
	        			break;
	         		case("6"):
	         			NameNode.instance.jtThread.displayRunningJobs();
	         			break;
	         		default:
	         			throw new Exception("Invalid Input detected: " + choice);
         		}
			} catch(Exception e){
				Logger.log(e.getMessage());
			}//end of try
		}//end of while
	}
}
