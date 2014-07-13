package filesystem;

import java.util.ArrayList;
import java.util.Arrays;

public class FileSystem {
	public static final char DIRECTORYSEPARATOR = '/';
	
	private Directory rootDirectory;
	
	public FileSystem(){
		rootDirectory = new Directory();
	}
	private Directory getPreviousWorkingDirectory(String[] treePath) throws FileSystemException{
		if(treePath.length == 0 |treePath.length == 1)
			return rootDirectory;
		return rootDirectory.getSubDirectory(Arrays.copyOfRange(treePath, 0, treePath.length-1));
	}

	private Directory getThisWorkingDirectory(String[] treePath) throws FileSystemException{
		if(treePath.length == 0)
			return rootDirectory;
		return rootDirectory.getSubDirectory(treePath);
	}
	
	public void MakeDirectory(String pathToDirectory) throws FileSystemException {
		String pathNodes[] = pathToDirectory.split( Character.toString(DIRECTORYSEPARATOR));
		
		getPreviousWorkingDirectory(pathNodes).MakeDirectory(pathNodes[pathNodes.length-1]);
	}
	
	public void InsertFile(String fileLocation, DistributedFile file) throws FileSystemException{
		String pathNodes[] = fileLocation.split( Character.toString(DIRECTORYSEPARATOR));
		
		getPreviousWorkingDirectory(pathNodes).AddFile(fileLocation.substring(1), file);
	}
	
	public void RemoveFile(String pathToFile) throws FileSystemException{
		String pathNodes[] = pathToFile.split( Character.toString(DIRECTORYSEPARATOR));
		
		getPreviousWorkingDirectory(pathNodes).RemoveFileOrDirectory(pathNodes[pathNodes.length-1]);
	}
	
	public ArrayList<String> ReturnFileList(String pathToFile) throws FileSystemException{
		String pathNodes[] = pathToFile.split( Character.toString(DIRECTORYSEPARATOR));
		
		return getThisWorkingDirectory(pathNodes).getList(pathNodes[pathNodes.length-1]);
	}
}
