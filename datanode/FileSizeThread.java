package datanode;

import java.io.File;

import commons.Logger;

public class FileSizeThread extends Thread {

	@Override 
	public void run(){
		while(true){
			//referred to http://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
			File folder = new File(DataNode.rootPath.toString());
			File[] listOfFiles = folder.listFiles();
			
			if(listOfFiles==null){
				Logger.log("FILE DOES NOT EXIST OR WRONG PATH");
				System.exit(0);
			}
			long sizeOfFiles = 0;
			DataNode.setFreeSpace(folder.getFreeSpace());
			for (int i = 0; i < listOfFiles.length; i++) {
				sizeOfFiles += listOfFiles[i].length();
			}
			DataNode.setSizeOfFilesStored(sizeOfFiles);
			//Logger.log("calculating filesize: " + DataNode.getSizeOfFilesStored() + " free: " + DataNode.getFreeSpace());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
