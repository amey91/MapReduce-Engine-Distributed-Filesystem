package namenode;

// special type of exception 
public class InvalidDataNodeException extends Exception {

	private static final long serialVersionUID = -8180190560166276675L;

	public InvalidDataNodeException(String message) {
        super(message);
    }
	public InvalidDataNodeException() {
        super("Invalid Data Node Exception");
    }
}
