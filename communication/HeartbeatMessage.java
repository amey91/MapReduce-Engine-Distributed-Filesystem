package communication;


// type of message
public class HeartbeatMessage extends Message {

	private static final long serialVersionUID = -431708953725366953L;
	
	public double percent;
	public Boolean complete;
	
	public HeartbeatMessage(double percent, Boolean complete) {
		
		super("Heartbeat");
		this.percent = percent;
		this.complete = complete;
		// TODO Auto-generated constructor stub
	}

}
