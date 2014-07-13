package filesystem;

import java.util.ArrayList;
import java.util.Arrays;

public class Directory {
	String directoryName;
	Directory parentDirectory;
	ArrayList<Directory> childDirectories;
	ArrayList<DistributedFile> files;
	
	
	public Directory(Directory parent, String directoryName) {
		this.parentDirectory = parent;
		this.directoryName = directoryName;
	}
	
	public Directory() {
		this.parentDirectory = null;
		this.directoryName = "";
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
		if(pathNodes.length==0)
			return this;
		for(Directory d: childDirectories)
			if(d.getName() == pathNodes[0])
				return getSubDirectory(Arrays.copyOfRange(pathNodes, 1, pathNodes.length));

		throw new FileSystemException("Invalid Path");
	}
	
	public void MakeDirectory(String directoryName) throws FileSystemException{
			
		if(isPresent(directoryName))
			throw new FileSystemException("Directory Already Exists");
		
		
		Directory childDirectory = new Directory(this, directoryName);
		childDirectories.add(childDirectory);
		
	}
	
	public void AddFile(String filePath, DistributedFile file) throws FileSystemException{
		
		if(isPresent(filePath))
			throw new FileSystemException("File Already Exists");
		
		file.parent = this;
		file.fileName = filePath;
		files.add(file);
	}
	private Boolean isPresent(String FileOrDirectoryName)
	{
		for(DistributedFile f: files)
			if(f.getFileName() == FileOrDirectoryName)
				return true;
		
		for(Directory d: childDirectories)
			if(d.getName() == FileOrDirectoryName)
				return true;
		
		return false;
	}
	private Boolean isEmpty()
	{
		return childDirectories.size() == 0 && files.size() == 0;
	}
	
	public void RemoveFileOrDirectory(String filePath) throws FileSystemException {
		int index = 0;
		for(DistributedFile f: files){
			if(f.fileName == filePath){
				f.delete();
				files.remove(index);
				return;
			}
		}
			
		index = 0;
		for(Directory d: childDirectories) {
			if(d.getName() == filePath) {
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

	public ArrayList<String> getList(String pathToDirectory) throws FileSystemException {
		
		ArrayList<String> returnList = new ArrayList<String>();
		for(Directory d: childDirectories)
			returnList.add("DIR  " + d.getName());
		
		for(DistributedFile f: files)
			returnList.add("FILE " + f.getFileName());
		
		return returnList;
	}
}
