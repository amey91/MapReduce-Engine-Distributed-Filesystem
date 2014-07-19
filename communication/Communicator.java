package communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
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

import commons.AddressToIPPort;
import commons.Logger;

// this class was created by me in the last DS assignment. I have reused it almost completely
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
	
	// create socket, accept message and close the socket
	public static Message sendAndReceiveMessage(Socket socket, Message inputMessage) throws InterruptedException, UnknownHostException, IOException, ClassNotFoundException {
		Communicator.sendMessage(socket, inputMessage);
		Message newObj = (Message)Communicator.receiveMessage(socket);
		socket.close();
		return newObj;
	}
	
	public static long receiveStream(Socket socket, FileOutputStream fos, long fileSize) throws IOException{
		BufferedOutputStream bos = new BufferedOutputStream(fos);
	    

	    byte[] bytearray = new byte[1024*1024];
	    InputStream is = socket.getInputStream();

	    long bytesLeft = fileSize;
	    
	    while(bytesLeft>0){
	    	
			int bytesToRead = bytearray.length;
			if(bytesLeft < bytearray.length)
				bytesToRead = (int)bytesLeft;
			
	    	int bytesRead = is.read(bytearray, 0, bytesToRead);
	    	
	    	if(bytesRead==0)
	    		break;
	    	
	        bos.write(bytearray, 0, bytesRead);
	        System.out.println(" "+bytesRead);;
	        bytesLeft -= bytesRead;
		}
	    bos.close();
	    return fileSize-bytesLeft;
	}
	
	public static long receiveFile(Socket socket, String filePath, long fileSize) throws IOException{
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(filePath);
		}catch(FileNotFoundException e){
			throw new IOException("Invalid output file location:"+ filePath);
		}
		
	    long fileSizeReceived = receiveStream(socket, fos, fileSize);
	    fos.close();
	    Logger.log("received: "+filePath+ ":" + fileSizeReceived+"/"+fileSize);
	    return fileSizeReceived;
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
	
	public static long sendStream(Socket socket, BufferedInputStream bis,
			long streamLength) throws IOException {
		Socket[] s = new Socket[1];
		s[0] = socket;
		return sendStream(s, bis, streamLength);
	}
	public static long sendStream(Socket[] socket, BufferedInputStream bis,
			long streamLength) throws IOException {
		
		long totalTransferred = 0;
		byte[] byteArray = new byte[102400];

		OutputStream os[] = new OutputStream[socket.length];
		for(int i=0;i<socket.length;i++)
			os[i] = socket[i].getOutputStream();
		
		while( totalTransferred < streamLength)
		{
			long left = (streamLength-totalTransferred);
			int bytesToRead = byteArray.length;
			if(left < byteArray.length)
				bytesToRead = (int)left;
			
			int bytesRead = bis.read(byteArray, 0, bytesToRead);
			
			if(bytesRead==0){
				break;
			}
				
			
			Logger.log("sending " + bytesRead + " " +totalTransferred);

			//for(int i=0; i<os.length; i++)
			os[0].write(byteArray, 0, bytesRead);
			
			totalTransferred += bytesRead;
		}//end of while

		Logger.log("out of send ");
		return totalTransferred;
	}

	public static Socket CreateTaskSocket(String clientKey) throws IOException {
		String[] ipPort = AddressToIPPort.addressToIPPort(clientKey);
			
		String ip = ipPort[0];
		int port = Integer.parseInt(ipPort[2]);
		Socket sendingSocket = new Socket(InetAddress.getByName(ip),port);
		return sendingSocket;
	}
	
	public static Socket CreateDataSocket(String clientKey) throws IOException {
		String[] ipPort = AddressToIPPort.addressToIPPort(clientKey);
			
		String ip = ipPort[0];
		int port = Integer.parseInt(ipPort[1]);
		Socket sendingSocket = new Socket(InetAddress.getByName(ip),port);
		return sendingSocket;
	}	
}
