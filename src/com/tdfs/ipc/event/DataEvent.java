package com.tdfs.ipc.event;

import java.net.Socket;
import java.util.Observable;

import com.tdfs.ipc.element.DataPacket;
import java.util.Observer;

/**
 * @author     gisripa
 */
public class DataEvent extends Observable{
	
	
	/**
	 */
	private Socket eventSocket;
	
		
	public void setDataPacket(Socket socket,DataPacket<?> dataPacket)
	{
		
		this.eventSocket = socket;
		setChanged();
		notifyObservers(dataPacket);
	}
	
	
	
	/**
	 * @return
	 */
	public Socket getEventSocket()
	{
		return eventSocket;
	}
	
}
