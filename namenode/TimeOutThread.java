package namenode;

import commons.Logger;

public class TimeOutThread extends Thread{
	@Override 
	public void run(){
		while(true){
			int i = 0;
			for(DataNodeInfo d : NameNode.instance.dataNodeList){
				if(Math.abs(d.getLastSeen()-System.currentTimeMillis())>conf.Constants.DATANODE_TIMEOUT){
					Logger.log("Client "+ d.getId() + " TIMED OUT. Deleteing it.");
					String id = d.getId();
					d.shutDown();
					NameNode.instance.dataNodeList.remove(i);

					NameNode.fs.HandleNodeFailure(id);
					
					NameNode.instance.displayDataNodes();
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
