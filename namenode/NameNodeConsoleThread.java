package namenode;

import java.io.BufferedReader;
import java.io.File;
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
				Logger.log("Please \"5\" to view a map of registered DataNodes");
				choice = br.readLine();
				if(choice=="" || choice==null){
         			throw new Exception("Blank input not allowed.");
         		}
         		switch(choice){
	         		case("5"):
	         			NameNode.displayDataNodes();
	        			break;
	         		default:
	         			throw new Exception("Invalid Input detected: " + choice);
         		}
			} catch(Exception e){
				Logger.log(e.getMessage());
				Logger.log("NameNode only accepts 5 as input... \n");
			}//end of try
		}//end of while
	}
}
