package filesystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import commons.Logger;

public class Directory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7072072642396423372L;
	
	String directoryName;
	Directory parentDirectory;
	ArrayList<Directory> childDirectories;
	ArrayList<DistributedFile> files;
	ArrayList<String> fileProxies;
	
	
	public Directory(Directory parent, String directoryName) {
		this.parentDirectory = parent;
		this.directoryName = directoryName;
		childDirectories = new ArrayList<Directory>();
		files = new ArrayList<DistributedFile>();
		fileProxies = new ArrayList<String>();
	}
	
	public Directory() {
		this.parentDirectory = null;
		this.directoryName = "";
		childDirectories = new ArrayList<Directory>();
		files = new ArrayList<DistributedFile>();
		fileProxies = new ArrayList<String>();
	}
	
	public String getName(){
		return directoryName;
	}
	
	public String getFullPath(){
		if(parentDirectory != null)
			return parentDirectory.getFullPath() + FileSystem.DIRECTORYSEPARATOR + getName();
		else
			return getName();
	}
	
	public Directory getSubDirectory(String[] pathNodes) throws FileSystemException{
		
		for(String s:pathNodes)
			Logger.log("getsub: " + s);
		int pos = 0;
		while( pos<pathNodes.length && pathNodes[pos].trim().equals(""))
			pos++;
		
		if(pos == pathNodes.length)
			return this;
		
		Logger.log("gh: " + pos);
		
		for(Directory d: childDirectories)
			if(d.getName().equals(pathNodes[pos]))
				return d.getSubDirectory(Arrays.copyOfRange(pathNodes, pos+1, pathNodes.length));
		
		Logger.log("throwing Exception" + pathNodes[pos] );
		throw new FileSystemException("Invalid Path");
	}
	
	public void MakeDirectory(String newDirectoryName) throws FileSystemException{
			
		if(isPresent(newDirectoryName, true))
			throw new FileSystemException("File/Directory Already Exists");
		
		
		Directory childDirectory = new Directory(this, newDirectoryName);
		childDirectories.add(childDirectory);
		
	}
	
	public void AddFile(String fileName, DistributedFile file) throws FileSystemException{
		
		if(isPresent(fileName, false))
			throw new FileSystemException("File/Directory Already Exists");
		

		if(!isPresent(fileName, true))
			throw new FileSystemException("Internal Error! Proxy for the file not found!");
		
		fileProxies.remove(fileName);
		
		file.parent = this;
		file.fileName = fileName;
		files.add(file);
	}
	
	private Boolean isPresent(String FileOrDirectoryName, Boolean includeProxy)
	{
		for(DistributedFile f: files)
			if(f.getFileName().equals(FileOrDirectoryName) )
				return true;

		for(Directory d: childDirectories)
			if(d.getName().equals(FileOrDirectoryName))
				return true;
		
		if(includeProxy)
			for(String s: fileProxies)
				if(s.equals(FileOrDirectoryName))
					return true;
		
		return false;
	}
	private Boolean isEmpty(){
		return childDirectories.size() == 0 && files.size() == 0 && fileProxies.size() == 0;
	}
	
	public void RemoveFileOrDirectory(String fileName) throws FileSystemException {
		int index = 0;
		for(DistributedFile f: files){
			if(f.fileName.equals(fileName)){
				f.delete();
				files.remove(index);
				return;
			}
		}
			
		index = 0;
		for(Directory d: childDirectories) {
			if(d.getName().equals(fileName)) {
				if(d.isEmpty()) {
					childDirectories.remove(index);
					return;
				}
				else
					throw new FileSystemException("Directory not empty");
			}
			index++;
		}
		
		throw new FileSystemException("File/Directory Not Found");
		
	}

	public ArrayList<String> getList() throws FileSystemException {
		
		ArrayList<String> returnList = new ArrayList<String>();
		for(Directory d: childDirectories)
			returnList.add("DIR   " + d.getName());

		for(DistributedFile f: files)
			returnList.add("FILE  " + f.getFileName());
		
		for(String s: fileProxies)
			returnList.add("PROXY " + s);
		
		Logger.log("lendir: " + (childDirectories.size()+files.size()));
		return returnList;
	}

	public void AddFileProxy(String fileName) throws FileSystemException {

		if(isPresent(fileName, true))
			throw new FileSystemException("File/Directory already exists");
		
		fileProxies.add(fileName);
	}
	
	public void RemoveFileProxy(String fileName) throws FileSystemException {

		if(!fileProxies.contains(fileName))
			throw new FileSystemException("Proxy doesn't exist");
		
		fileProxies.remove(fileName);
	}

	public FileBlock[] getFileBlocks(String fileName) throws FileSystemException {
		
		for(DistributedFile f: files)
			if(f.fileName.equals(fileName))
				return f.blocks;
		throw new FileSystemException("File Not Found");
	}

	public void FixBlocksWith(String id) {
		for(DistributedFile f: files)
			for(FileBlock b: f.blocks){
				try {
					b.fix(id);
				} catch (FileSystemException e) {
					// TODO delete
					Logger.log("Couldn't fix: " + f.getFullPath());
					e.printStackTrace();
					break;
				}
			}
	}

	public DistributedFile getFile(String fileName) throws FileSystemException {
		for(DistributedFile f: files)
			if(f.getFileName().equals(fileName))
				return f;
		throw new FileSystemException(fileName + "File Not Found");
	}
	
}
