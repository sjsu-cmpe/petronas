package com.tdfs.ipc.io;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import com.tdfs.interfaces.ipc.IPCClient;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;

public abstract class AbstractClient implements IPCClient{

	protected Socket requestSocket;
	private ObjectOutputStream outgoingData;
	private ObjectInputStream incomingData;
	private InetAddress host;
	private int port;
	
	private static Logger logger = Logger.getLogger(AbstractClient.class);
	
	protected AbstractClient(InetAddress host,int port)
	{
		this.host = host;
		this.port = port;
		
	}
	
	@Override
	public boolean initiateConnection() {
		boolean connectionSuccessful = false;
		try{
			requestSocket = new Socket();
			requestSocket.connect(new InetSocketAddress(this.host,this.port), 5000);
			logger.info(new StringBuilder("Connected to -->").append(host).append(",").append(port));
			connectionSuccessful = true;
		}
		catch(IOException ioe)
		{
			logger.error("Exception occurred in initiating Client Connection", ioe);
			connectionSuccessful = false;
		}
		
		return connectionSuccessful;
		
		
	}




	@Override
	public void finishConnection() {
		try{
			incomingData.close();
			outgoingData.close();
			requestSocket.close();
		}
		catch(IOException ioe)
		{
			//TODO: ERROR HANDLING AND LOGGING
		}
		
	}
	
	public void sendMessage(DataPacket<?> dataPacket)
	{
		try {
			
			outgoingData = new ObjectOutputStream(requestSocket.getOutputStream());
			outgoingData.writeObject(dataPacket);
			outgoingData.flush();
			
		} catch (IOException e) {
			logger.error("Exception occurred while sending Message to Server",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public DataPacket<?> responseHandler()
	{
		
		DataPacket<?> dataPacket = null;
 		try{
			incomingData = new ObjectInputStream(requestSocket.getInputStream());
			dataPacket = (DataPacket<?>) incomingData.readObject();
		}
		catch(IOException ioe)
		{
			logger.error("Exception occurred while handling response from Server", ioe);
			
			
		} catch (ClassNotFoundException e) {
			logger.error("Exception while casting class to DataPacket<?>", e);
			
		}
		return dataPacket;
	}
	
	
	
	
	
}
