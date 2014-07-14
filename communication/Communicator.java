package communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import commons.Logger;
import conf.Constants;


public class Communicator {
	
	// accept created socket and do not close it after use
	public static void sendMessage(Socket sendingSocket, Message m) throws InterruptedException, IOException {
		if(sendingSocket == null)
			throw new IOException("Communicator received invalid socket");
		ObjectOutputStream os = new ObjectOutputStream(sendingSocket.getOutputStream());
		os.writeObject(m);
		Thread.sleep(200);
	}

	// create the socket, send message and then close the socket
	public static void sendMessage(String hostName, int port, Message m) throws InterruptedException, UnknownHostException, IOException {
		Socket sendingSocket = new Socket(InetAddress.getByName(hostName),port);
		sendMessage(sendingSocket, m);
		//close sending socket
		sendingSocket.close();
	}
	
	// take socket, receive message and do not close socket
	public static Message receiveMessage(Socket receivingSocket) throws InterruptedException, IOException, ClassNotFoundException {
		ObjectInputStream is = new ObjectInputStream(receivingSocket.getInputStream());
		Object newObj = (Object)is.readObject();
		
		if (newObj == null){
            throw new IOException("Received a null message");
        }
		return (Message)newObj;
	}
	
	// create socket, accept message and close the socket
	public static Message sendAndReceiveMessage(String hostName, int port, Message inputMessage) throws InterruptedException, UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = new Socket(hostName,port);
		Communicator.sendMessage(socket, inputMessage);
		Message newObj = (Message)Communicator.receiveMessage(socket);
		socket.close();
		return newObj;
	}
	
	public static void receiveFile(Socket socket, String filePath, long fileSize) throws IOException{
		FileOutputStream fos = new FileOutputStream(filePath);
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	    

	    byte[] bytearray = new byte[1024];
	    InputStream is = socket.getInputStream();

	    long bytesLeft = fileSize;
	    
	    while(bytesLeft>0){
	    	int bytesRead = is.read(bytearray, 0, bytearray.length);
	        bos.write(bytearray, 0, bytesRead);
	        System.out.println(" "+bytesRead);;
	        bytesLeft -= bytesRead;
		}
	    bos.close();
	    fos.close();
	}
	
	// create server socket, keep listening for requests, create thread for handling message
	public static void listenForMessages(ServerSocket listeningSocket, Object input, Class<?> T)  {
		//
		
		Constructor<?> constructorNew = null;
		try {
			constructorNew = T. getConstructor(Object.class, Socket.class);
			 	
			Logger.log(T.getName()+ " listening on "+ listeningSocket.getInetAddress().getHostAddress()+ ":"+ listeningSocket.getLocalPort());

		} catch (NoSuchMethodException e1) {
			Logger.log("Method call failed or you entered invalid data.");
			return;
		}
		
		while(true){
			// setup the socket for the server that listens to clients 
			try {
				System.out.println(T.getName() + " waiting for new message...");
				Socket clientSocket = listeningSocket.accept();
				Thread instance = (Thread)constructorNew.newInstance(input, (Object)clientSocket);
				new Thread(instance).start();
				
			} catch (IOException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				System.out.println("Error while opening port at registry server");				
			}
		}//end of true	
	}

	public static long sendStream(Socket[] socket, BufferedInputStream bis,
			long streamLength) throws IOException {
		
		long totalTransferred = 0;
		byte[] byteArray = new byte[1024];

		OutputStream os[] = new OutputStream[socket.length];
		
		while( totalTransferred < streamLength)
		{
			int left = (int) (streamLength-totalTransferred);
			int bytesRead = bis.read(byteArray, 0, Math.min(byteArray.length, left) );
			
			if(bytesRead==0)
				break;
			
			Logger.log("sending " + bytesRead);
			for(int i=0; i<os.length; i++)
				os[i].write(byteArray, 0, bytesRead);
			totalTransferred += bytesRead;
		}
		return totalTransferred;
	}	
}
