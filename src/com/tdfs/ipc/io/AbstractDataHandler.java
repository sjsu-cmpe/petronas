package com.tdfs.ipc.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.tdfs.ipc.element.DataPacket;

@Deprecated
public abstract class AbstractDataHandler implements Runnable{
	
	
	protected DataPacket<?> dataPacket;
	protected ObjectOutputStream outStream;
	protected AbstractServer server;
	public AbstractDataHandler(DataPacket<?> dataPacket, AbstractServer server)
	{
		
		this.dataPacket = dataPacket;
		this.server = server;
	}
	
	public void sendResponse(Socket outgoingSocket, DataPacket<?> dataPacket)
	{
		
		try{
			outStream = new ObjectOutputStream(outgoingSocket.getOutputStream());
			outStream.writeObject(dataPacket);
			outStream.flush();
		}
		catch(IOException ioe)
		{
			//TODO: Exception handling and loggin
			ioe.printStackTrace();
		}
		finally{
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
