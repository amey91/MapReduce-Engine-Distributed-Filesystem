package namenode;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;

import commons.Logger;
import filesystem.DeleteFileThread;
import filesystem.DistributedFile;
import filesystem.FileBlock;
import filesystem.FileSystem;
import filesystem.FileSystemException;

public class NameNode extends Thread implements NameNodeInterface {
	// register does not return id for mapper since ID = ip, port
	
	public static ArrayList<DataNodeInfo> dataNodeList = new ArrayList<DataNodeInfo>();
	
	public static int currentBlockNumber = 0;
	public static DeleteFileThread deleteThread;
	
	static FileSystem fs = new FileSystem();
	
	public ArrayList<String> ls(String dirPath)throws RemoteException, FileSystemException{
		ArrayList<String> list =  fs.ReturnFileList(dirPath);
		for(String s:list)
			Logger.log("In Path List: "+s);
		return list;
	}
	public void mkdir(String newDirName)throws RemoteException, FileSystemException{
		fs.MakeDirectory(newDirName);
	}
	public void rm(String dirPath)throws RemoteException, FileSystemException{
		fs.RemoveFile(dirPath);
	}
	// @return: a list of strings containing the blocks and 
	// 			the intended location of each block on various mappers 
	public FileBlock[] localToHDFS(String key, String newDFSFileName, long fileSize)throws RemoteException, FileSystemException{
		// figure out number of blocks required.
		//TODO add check for valid newDFSFileName
		long size1 = fileSize/conf.Constants.MIN_BLOCK_SIZE;
		long size2 = NameNode.dataNodeList.size();
		
		//take maximum out of these values so that no. of blocks are minimized
		long noOfBlocks = Math.min(size1, size2);
		// check if file is smaller than min lock size
		if(fileSize<=conf.Constants.MIN_BLOCK_SIZE)
			noOfBlocks = 1;
		
		fs.InsertFileProxy(newDFSFileName);
		
		for(DataNodeInfo d : NameNode.dataNodeList)
			if(d.getId().equals(key))
				d.addFileProxy(newDFSFileName);
		// determine where these blocks will be placed
		// take the blocks which currently store the lowest values
		return getFileAllocation(newDFSFileName,(int)noOfBlocks); 
		
	}
	public void confirmLocalToHDFS(String clientKey, String fileName, FileBlock[] blocks, long[] blockSizes)throws RemoteException, FileSystemException{
		//TODO update datanodeinfo
		DistributedFile file = new DistributedFile(blocks);
		fs.InsertFile(fileName, file);
		
		for(DataNodeInfo d : NameNode.dataNodeList)
			if(d.getId().equals(clientKey))
				d.deleteFileProxy(fileName);
	}
	// TODO 
	public void HDFSToLocal(String fileName)throws RemoteException{
		
	}
	@Override
	public void register(String myKey) throws RemoteException {
		//check if any of the params is not valid
		if(myKey==null || myKey==""){
			Logger.log("Received invalid Key: " + myKey);
			return;
		}
		
		DataNodeInfo info = new DataNodeInfo(myKey);
		if(NameNode.dataNodeList.contains(info)){
			throw new RemoteException("DataNode with duplicate key "+ myKey +" trying to register!");
		}
		dataNodeList.add(info);
		Logger.log("New datanode added: "+ myKey);
	}
	@Override
	public void Heartbeat(String clientKey, long sizeOfStoredFiles, long freeSpace) throws RemoteException {
			
			//Logger.log("Got heartbeat for: "+clientKey);
			for(DataNodeInfo d : NameNode.dataNodeList){
				if(d.getId().equals(clientKey))
					d.setLastSeen(System.currentTimeMillis());
					d.setFreeSpace(freeSpace);
					d.setsizeOfStoredFiles(sizeOfStoredFiles);
			}
	}
	
	@Override
	public String test() throws RemoteException {
		return "RMI IS NOW WORKING!!";
	}
	
	public static void main(String args[]){
		
		try{
			//@referred http://docs.oracle.com/javase/7/docs/technotes/guides/rmi/hello/hello-world.html#define`
			NameNode nameNodeObj = new NameNode();			 
            NameNodeInterface stub = (NameNodeInterface) UnicastRemoteObject.exportObject(nameNodeObj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("RMI", stub);
            System.err.println("Server ready");
            new Thread(new NameNodeConsoleThread()).start();
            new Thread(new TimeOutThread()).start();
            deleteThread =  new DeleteFileThread();
            deleteThread.start();
		} catch(Exception e){
			System.out.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
		
	}
	public static void displayDataNodes() {
		for(DataNodeInfo node : dataNodeList){
			Logger.log("Key: "+ node.getId() + " | STORING: " + node.getsizeOfStoredFiles() + " | FREE: "+ node.getFreeSpace());
		}
		
	}
	
	private static FileBlock[] getFileAllocation(String DFSFileName, int no_of_blocks){
		Logger.log("no of blocks:" + no_of_blocks);
		
		Collections.sort(dataNodeList);
		Logger.log("Nodes after sorting: ");
		for(DataNodeInfo s: dataNodeList)
			Logger.log(s.getId()+ " | Stores: " + s.getsizeOfStoredFiles() +" | FREE: "+ s.getFreeSpace());
		
		int iter = 0;
		FileBlock[] allocation = new FileBlock[no_of_blocks];
		
		//TODO synchronization failure
		for(int i=0;i<no_of_blocks;i++){
			allocation[i] = new FileBlock( "FILE" + (currentBlockNumber++));
		}
		
		try
		{
			//TODO Use a more sophisticated load balancing strategy later 
			while(iter<no_of_blocks && dataNodeList.iterator().hasNext()){
				//allocatedBlocks += dataNodeList.get(iter).getId()+" ";
				
				for(int j=0; j<3;j++)
					allocation[(iter+j)%no_of_blocks].addNodeLocation(dataNodeList.get(iter).getId());
					
				iter++;
			}
			//allocation[0] = new FileBlock(DFSFileName+"_"+NameNode.currentBlockNumber++, allocatedBlocks);
		}
		catch(Exception e){
			e.printStackTrace();
			//TODO delete
		}
		
		for(int i=0;i<no_of_blocks;i++){
			Logger.log(i+": "+ allocation[i].getNodeLocations()[0] + allocation[i].getNodeLocations()[1] + allocation[i].getNodeLocations()[2]);
			
			
		}
		return allocation;
	}
	@Override
	public void ConfirmDeletion(String blockName, String nodeLocation)
			throws RemoteException {
		deleteThread.remove(blockName, nodeLocation);
		
	}

}
