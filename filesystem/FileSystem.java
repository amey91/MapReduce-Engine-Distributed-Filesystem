package filesystem;

import java.util.ArrayList;
import java.util.Arrays;

public class FileSystem {
	public static final char DIRECTORYSEPARATOR = '/';

	private Directory rootDirectory;

	Object lock = new Object();

	public FileSystem(){
		synchronized(lock){
			rootDirectory = new Directory();
		}
	}
	private Directory getPreviousWorkingDirectory(String[] treePath) throws FileSystemException{
		if(treePath.length == 0)
			throw new FileSystemException("Invalid Path");
		
		return rootDirectory.getSubDirectory(Arrays.copyOfRange(treePath, 0, treePath.length-1));
	}

	private Directory getThisWorkingDirectory(String[] treePath) throws FileSystemException{
		return rootDirectory.getSubDirectory(treePath);
	}

	public void MakeDirectory(String pathToDirectory) throws FileSystemException {
		synchronized(lock){
			String pathNodes[] = pathToDirectory.split( Character.toString(DIRECTORYSEPARATOR));

			getPreviousWorkingDirectory(pathNodes).MakeDirectory(pathNodes[pathNodes.length-1]);
		}
	}

	public void InsertFile(String fileLocation, DistributedFile file) throws FileSystemException{
		synchronized(lock){
			String pathNodes[] = fileLocation.split( Character.toString(DIRECTORYSEPARATOR));
			getPreviousWorkingDirectory(pathNodes).AddFile(pathNodes[pathNodes.length-1], file);
		}
	}

	public void RemoveFile(String pathToFile) throws FileSystemException{
		synchronized(lock){
			String pathNodes[] = pathToFile.split( Character.toString(DIRECTORYSEPARATOR));

			getPreviousWorkingDirectory(pathNodes).RemoveFileOrDirectory(pathNodes[pathNodes.length-1]);
		}
	}

	public ArrayList<String> ReturnFileList(String pathToFile) throws FileSystemException{
		synchronized(lock){
			String pathNodes[] = pathToFile.split( Character.toString(DIRECTORYSEPARATOR));

			return getThisWorkingDirectory(pathNodes).getList();
		}
	}
	public void InsertFileProxy(String pathToFile) throws FileSystemException{
		synchronized(lock){
			String pathNodes[] = pathToFile.split( Character.toString(DIRECTORYSEPARATOR));
			getPreviousWorkingDirectory(pathNodes).AddFileProxy(pathNodes[pathNodes.length-1]);
		}
	}
	public void RemoveFileProxy(String pathToFile) throws FileSystemException{
		synchronized(lock){
			String pathNodes[] = pathToFile.split( Character.toString(DIRECTORYSEPARATOR));
			getPreviousWorkingDirectory(pathNodes).RemoveFileProxy(pathNodes[pathNodes.length-1]);
		}
	}
}
