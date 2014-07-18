package jobhandler;

public interface JobNode {
	public String[] monitor();
	public int stop(int jobId);
	public int start(JarContainer newJob);
	
	
}
