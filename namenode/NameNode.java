package namenode;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import mapreduce.Job;

import commons.Logger;

import conf.Constants;
import filesystem.DistributedFile;
import filesystem.FileBlock;
import filesystem.FileSystem;
import filesystem.FileSystemException;

public class NameNode extends Thread implements NameNodeInterface {
	// register does not return id for mapper since ID = ip, port

	public ArrayList<DataNodeInfo> dataNodeList = new ArrayList<DataNodeInfo>();

	public int currentBlockNumber = 0;
	public DeleteFileThread deleteThread;

	static FileSystem fs = new FileSystem();
	public static NameNode instance;
	
	JobTrackerThread jtThread;
	private void checkKey(String clientKey) throws InvalidDataNodeException{
		for(DataNodeInfo d: dataNodeList)
			if(d.getId().equals(clientKey))
				return;
		throw new InvalidDataNodeException();
	}
	
	private NameNode(){
	}
	
	public ArrayList<String> ls(String clientKey, String dirPath)throws RemoteException, FileSystemException, InvalidDataNodeException{
		checkKey(clientKey);

		ArrayList<String> list =  fs.ReturnFileList(dirPath);
		for(String s:list)
			Logger.log("In Path List: "+s);
		return list;
	}
	public void mkdir(String clientKey, String newDirName)throws RemoteException, FileSystemException, InvalidDataNodeException{
		checkKey(clientKey);

		fs.MakeDirectory(newDirName);
	}
	public void rm(String clientKey, String dirPath)throws RemoteException, FileSystemException, InvalidDataNodeException{
		checkKey(clientKey);

		fs.RemoveFile(dirPath);
	}
	// @return: a list of strings containing the blocks and 
	// 			the intended location of each block on various mappers 
	public FileBlock[] localToHDFS(String clientKey, String newDFSFileName, long fileSize)throws RemoteException, FileSystemException, InvalidDataNodeException{
		// figure out number of blocks required.
		//TODO add check for valid newDFSFileName
		checkKey(clientKey);

		long size1 = fileSize/conf.Constants.MIN_BLOCK_SIZE;
		long size2 = dataNodeList.size();

		//take maximum out of these values so that no. of blocks are minimized
		long noOfBlocks = Math.min(size1, size2);
		// check if file is smaller than min lock size
		if(fileSize<=conf.Constants.MIN_BLOCK_SIZE)
			noOfBlocks = 1;

		fs.InsertFileProxy(newDFSFileName);

		// store this fileblock into the temporary fileblocks so that origin failure is handled
		FileBlock[] resultBlock = getFileAllocation(newDFSFileName,(int)noOfBlocks);

		for(DataNodeInfo d : dataNodeList)
			if(d.getId().equals(clientKey)){
				d.addFileProxy(newDFSFileName);
				d.setFileBLocks(resultBlock);
			}



		// determine where these blocks will be placed
		// take the blocks which currently store the lowest values
		return  resultBlock;

	}
	public void confirmLocalToHDFS(String clientKey, Boolean success, String fileName, FileBlock[] blocks)throws RemoteException, FileSystemException, InvalidDataNodeException{
		checkKey(clientKey);

		Logger.log("Inside confirmlocaltohdfs: " + clientKey + " " + success + " " + fileName);
		if(success){
			DistributedFile file = new DistributedFile(blocks);
			fs.InsertFile(fileName, file);
		}
		else{
			for(FileBlock b: blocks)
				b.delete();
		}

		for(DataNodeInfo d : dataNodeList)
		{
			if(d.getId().equals(clientKey)){
				if(!success)
					fs.RemoveFileProxy(fileName);
				
				d.deleteTempFileBlock(blocks);
				d.deleteFileProxy(fileName);
				
			}
		}

	}
	@Override
	public void register(String myKey) throws RemoteException {
		//check if any of the params is not valid
		if(myKey==null || myKey==""){
			Logger.log("Received invalid Key: " + myKey);
			return;
		}

		DataNodeInfo info = new DataNodeInfo(myKey);
		if(dataNodeList.contains(info)){
			throw new RemoteException("DataNode with duplicate key "+ myKey +" trying to register!");
		}
		dataNodeList.add(info);
		Logger.log("New datanode added: "+ myKey);
	}
	@Override
	public void Heartbeat(String clientKey, long sizeOfStoredFiles, long freeSpace, 
			int freeProcesses, int totalProcesses) throws RemoteException, InvalidDataNodeException {
		
		checkKey(clientKey);

		for(DataNodeInfo d : dataNodeList){
			if(d.getId().equals(clientKey))
				d.setLastSeen(System.currentTimeMillis());
			d.setFreeSpace(freeSpace);
			d.setsizeOfStoredFiles(sizeOfStoredFiles);
			for(DataNodeInfo node : dataNodeList){
				if(node.getId().equals(clientKey)){
					node.setFreeProcesses(freeProcesses);
					node.setTotalProcesses(totalProcesses);
					break;
				}
			}
			
		}
	}

	@Override
	public int submitJob(String clientKey, Job j) throws RemoteException, InvalidDataNodeException, FileSystemException {
		checkKey(clientKey);
		return jtThread.addJob(j);
	}

	public static void main(String args[]){

		try{
			//@referred http://docs.oracle.com/javase/7/docs/technotes/guides/rmi/hello/hello-world.html#define`

			instance = new NameNode();			 
			NameNodeInterface stub = (NameNodeInterface) UnicastRemoteObject.exportObject(instance, 0);


			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("RMI", stub);
			System.err.println("Server ready");
			new Thread(new NameNodeConsoleThread()).start();
			new Thread(new TimeOutThread()).start();
			
			instance.deleteThread =  new DeleteFileThread();
			instance.deleteThread.start();
			instance.jtThread = new JobTrackerThread();
			instance.jtThread.start();
			
		} catch(Exception e){
			System.out.println("Server exception: " + e.toString());
			e.printStackTrace();
		}

	}
	public void displayDataNodes() {
		for(DataNodeInfo node : dataNodeList){
			Logger.log("Key: "+ node.getId() + " | STORING: " + node.getsizeOfStoredFiles() + " | FREE: "+ node.getFreeSpace());
		}

	}

	private FileBlock[] getFileAllocation(String DFSFileName, int no_of_blocks){
		Logger.log("no of blocks:" + no_of_blocks);

		Collections.sort(dataNodeList);
		Logger.log("Nodes after sorting: ");
		for(DataNodeInfo s: dataNodeList)
			Logger.log(s.getId()+ " | Stores: " + s.getsizeOfStoredFiles() +" | FREE: "+ s.getFreeSpace());

		FileBlock[] allocation = new FileBlock[no_of_blocks];

		//TODO synchronization failure
		for(int i=0;i<no_of_blocks;i++){
			allocation[i] = new FileBlock( "FILE" + (currentBlockNumber++));
		}

		try{
			Iterator<DataNodeInfo> iter = dataNodeList.iterator();
			//TODO Use a more sophisticated load balancing strategy later
			for(int i=0;i<Constants.REPLICATION_FACTOR*no_of_blocks;i++){
				if(!iter.hasNext())
					iter = dataNodeList.iterator();
				
				allocation[(i/Constants.REPLICATION_FACTOR)%no_of_blocks].addNodeLocation(iter.next().getId());
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
	public void ConfirmDeletion(String clientKey, String blockName, String nodeLocation)
			throws RemoteException, InvalidDataNodeException {
		checkKey(clientKey);

		deleteThread.remove(blockName, nodeLocation);

	}

	@Override
	public FileBlock[] getFileBlocks(String clientKey, String HDFSFilePath)
			throws FileSystemException, RemoteException, InvalidDataNodeException {
		checkKey(clientKey);
		
		return NameNode.fs.getFileBlocks(HDFSFilePath);
	}

	@Override
	public ArrayList<String> getNewLocations(String clientKey, ArrayList<String> doneList,
			ArrayList<String> failList) throws RemoteException,
			InvalidDataNodeException, FileSystemException {
		
		checkKey(clientKey);
		
		Collections.sort(dataNodeList);
		int numRequired = failList.size();
		ArrayList<String> returnList = new ArrayList<String>();
		for(DataNodeInfo d: dataNodeList){
			if(!doneList.contains(d.getId()) && !failList.contains(d.getId())){
				returnList.add(d.getId());
				if(returnList.size() >= numRequired)
					break;
			}
		}
		if(returnList.size() < numRequired)
			return null; //Dont have other options, send fail
		else
			return returnList;
	}

	public String findExecuteLocation(String[] nodeLocations) throws InvalidDataNodeException {		
		// if block is present on node and there is space left, select that node
		// check for all replication factors of the block
		int free = 0;
		String destination = null;
		for(int i = 0; i<nodeLocations.length;i++){
			checkKey(nodeLocations[i]);
			int nodeFreeProcesses =dataNodeList.get(dataNodeList.indexOf(nodeLocations[i])).getFreeProcesses(); 
			// choose most free
			if(free < nodeFreeProcesses){
				free = nodeFreeProcesses;
				destination = nodeLocations[i];
			}
		}
		if(free != 0 && !destination.equals(null))
			return destination;
		
		if(destination.equals(null))
			throw new InvalidDataNodeException("Null key found in scheduler algorithm");
		
		// if a node that does not have block is free, allocate
		free = 0;
		destination = null;
		for(DataNodeInfo d : dataNodeList){
			// get most free
			if(free<d.getFreeProcesses()){
				free = d.getFreeProcesses();
				destination = d.getId();
			}
		}
		
		if(free !=0 && !destination.equals(null))
			return destination;
		if(destination.equals(null))
			throw new InvalidDataNodeException("Null key found in scheduler algorithm");
		// if no nodes are free, enque job 
		
		return "-1";
	}

	@Override
	public void sendUpdate(String clientKey, Boolean mapperOrReducer, int jobId, int taskId,
			double percentComplete, Boolean complete) throws RemoteException, InvalidDataNodeException {
		checkKey(clientKey);
		jtThread.sendUpdate(clientKey, mapperOrReducer, jobId, taskId, percentComplete, complete);		
	}

}
