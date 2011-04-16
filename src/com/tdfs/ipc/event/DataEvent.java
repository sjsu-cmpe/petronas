package com.tdfs.ipc.event;

import java.net.Socket;
import java.util.Observable;

import com.tdfs.ipc.element.DataPacket;

public class DataEvent extends Observable{
	
	
	private Socket eventSocket;
	
		
	public void setDataPacket(Socket socket,DataPacket<?> dataPacket)
	{
		
		this.eventSocket = socket;
		setChanged();
		notifyObservers(dataPacket);
	}
	
	
	
	public Socket getEventSocket()
	{
		return eventSocket;
	}
	
}
