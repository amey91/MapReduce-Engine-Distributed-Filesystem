package jobhandler;

import java.io.File;
import java.io.Serializable;

public class JarContainer implements Serializable{
	private static final long serialVersionUID = -4855003548158730255L;
	
	// jar file to be run at namenode
	File jarFile;
}
