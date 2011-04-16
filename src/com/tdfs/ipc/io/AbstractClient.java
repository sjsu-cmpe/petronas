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

import com.tdfs.interfaces.ipc.IPCClient;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;

public abstract class AbstractClient implements IPCClient{

	protected Socket requestSocket;
	private ObjectOutputStream outgoingData;
	private ObjectInputStream incomingData;
	private InetAddress host;
	private int port;
	
	
	protected AbstractClient(InetAddress host,int port)
	{
		this.host = host;
		this.port = port;
		
	}
	
	@Override
	public void initiateConnection() {
		
		try{
			requestSocket = new Socket(this.host,this.port);
			//requestSocket.bind(new InetSocketAddress(9191));
			//TODO: LOGGING
			System.out.println(new StringBuilder("Connected to -->").append(host).append(",").append(port));
		}
		catch(IOException ioe)
		{
			//TODO: Exception Handling and Loggin
			ioe.printStackTrace();
		}
		
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public DataPacket<?> responseHandler()
	{
		
		try{
			incomingData = new ObjectInputStream(requestSocket.getInputStream());
			return (DataPacket<?>) incomingData.readObject();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			return null;
			
		} catch (ClassNotFoundException e) {
			// TODO EXCEPTION HANDLING AND LOGGIN
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
	
}
