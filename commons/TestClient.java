package commons;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class TestClient {
  public static void main(String[] argv) throws Exception {
    Socket sock = new Socket("127.0.0.1", 23456);
    
    
    byte[] mybytearray = new byte[1024];
    InputStream is = sock.getInputStream();
    FileOutputStream fos = new FileOutputStream("C:\\Users\\Amey\\workspace\\example3\\commons\\s.pdf");
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    
    int bytesRead = is.read(mybytearray, 0, mybytearray.length);;
    int i =0;
    while(bytesRead>0 && !sock.isClosed()){
        bos.write(mybytearray, 0, bytesRead);
    	bytesRead = is.read(mybytearray, 0, mybytearray.length);
        System.out.println(" "+bytesRead);
        if(i++==10)
        	break;
	}
    bos.close();
    sock.close();
  }
}

   