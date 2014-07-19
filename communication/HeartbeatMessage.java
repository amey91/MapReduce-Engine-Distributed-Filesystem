package communication;

import communication.Message;

public class HeartbeatMessage extends Message {

	public double percent;
	public Boolean complete;
	public HeartbeatMessage(String type, double percent, Boolean complete) {
		super(type);
		this.percent = percent;
		this.complete = complete;
		// TODO Auto-generated constructor stub
	}

}
