package com.gnaix.common.app;

import java.util.HashMap;
import java.util.Map;

public class ClientHandler {
	Map<Integer,Integer> mClientMap;
	
	/**
	 * The Constructors 
	 * 	 
	 */
	public ClientHandler()
	{
		mClientMap = new HashMap<Integer,Integer>();    	
	}
	
	public void addClient(int messageId,int clientCode)
	{
		mClientMap.put(messageId, clientCode);
	}
	
	public int getClient(int messageId)
	{
		Integer code = (Integer) mClientMap.get(messageId);
		
		if ( code == null )
			return 0;
		else
			return code;
	}
	
	public void removeClient(int messageId)
	{
		mClientMap.remove(messageId);
	}
	
}
