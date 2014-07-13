package filesystem;


public class DistributedFile {
	Directory parent;
	String fileName;
	FileBlock[] blocks;
	
	public DistributedFile(FileBlock[] blocks)
	{
		this.parent = null;
		this.fileName = null;
		this.blocks = blocks;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getFullPath(){
		return parent.getFullPath() + FileSystem.DIRECTORYSEPARATOR + fileName;
	}

	public void delete() {
		for(FileBlock b: blocks)
			b.delete();
	}
}
