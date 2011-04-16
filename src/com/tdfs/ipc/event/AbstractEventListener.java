package com.tdfs.ipc.event;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observer;

import com.tdfs.ipc.element.DataPacket;

public abstract class AbstractEventListener implements Observer{
	
	private ObjectOutputStream outStream;
	
	public AbstractEventListener()
	{
		
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
