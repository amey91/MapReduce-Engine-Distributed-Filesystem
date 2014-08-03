package communication;

public class MergeAndUploadMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2455706014204015397L;
	public String[] clients;
	public int jobId;
	public String HDFSFilePath;
	
	public MergeAndUploadMessage(int jobId, String[] clients, String HDFSFilePath) {
		super("MergeAndUpload");
		this.jobId = jobId;
		this.clients = clients;
		this.HDFSFilePath = HDFSFilePath;
	}

}
