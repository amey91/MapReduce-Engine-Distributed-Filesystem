package commons;

import java.net.UnknownHostException;

public class AddressToIPPort {
	public static String[] addressToIPPort(String address) throws UnknownHostException	{
			if(address == null || address == ""){
				throw new UnknownHostException("Invalid Network Address");
			}
			return address.split(":");
		}
}

