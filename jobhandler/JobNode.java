package jobhandler;

public interface JobNode {
	public void heartBeat(int nodeId);
	public String[] monitor();
	public int stop(int jobId);
	public int start(JarContainer newJob);
}
