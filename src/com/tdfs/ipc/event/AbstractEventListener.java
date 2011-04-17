package com.tdfs.ipc.event;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.tdfs.ipc.element.DataPacket;

public abstract class AbstractEventListener implements Observer{
	
	private ObjectOutputStream outStream;
	
	private static Logger logger = Logger.getLogger(AbstractEventListener.class);
	
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
			logger.error("Exception occurred in sending response in AbstractEventListener", ioe);
		}
		finally{
			
			closeStream();
		}
	}
	
	private void closeStream()
	{
		try {
			outStream.close();
		} catch (IOException e) {
			logger.error("Exception occurred in closing the stream in AbstractEventListener", e);
		}
	}
}
