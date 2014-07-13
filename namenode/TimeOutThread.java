package namenode;

import commons.Logger;

public class TimeOutThread extends Thread{
	@Override 
	public void run(){
		while(true){
			int i = 0;
			for(DataNodeInfo d : NameNode.dataNodeList){
				if(Math.abs(d.getLastSeen()-System.currentTimeMillis())>10000){
					// TODO Failure! Handle Files placed on this machine
					Logger.log("Client "+ d.getId() + " TIMED OUT. Deleteing it.");
					NameNode.dataNodeList.remove(i);
					NameNode.displayDataNodes();
					break;
				}
				i++;
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Logger.log("TimeOut Thread was interrupted.");
				e.printStackTrace();
			}
		}
	}
}
