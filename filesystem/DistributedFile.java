package filesystem;

import java.io.Serializable;

// a representation of my distributed file and things required to identify and retrieve it
public class DistributedFile implements Serializable{

	private static final long serialVersionUID = -4449361261647377875L;
	Directory parent;
	String fileName;
	FileBlock[] blocks;
	
	public DistributedFile(FileBlock[] blocks){
		this.parent = null;
		this.fileName = null;
		this.blocks = blocks;
	}

	public String getFileName() {
		return fileName;
	}
	
	public FileBlock[] getFileBlocks() {
		return blocks;
	}
	
	public String getFullPath(){
		return parent.getFullPath() + FileSystem.DIRECTORYSEPARATOR + fileName;
	}

	public void delete() {
		for(FileBlock b: blocks)
			b.delete();
	}
}
