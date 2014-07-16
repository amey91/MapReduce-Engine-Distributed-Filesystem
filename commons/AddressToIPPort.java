package commons;

import java.net.UnknownHostException;

public class AddressToIPPort {
	public static String[] addressToIPPort(String address) throws UnknownHostException	{
			if(address == null || address == "" || address.indexOf(':')==-1){
				throw new UnknownHostException("Invalid Network Address");
			}
			String[] split = address.split(":");
			if(split.length!=3)
				throw new UnknownHostException("Invalid Network Address"); 
			return split;
		}
}

