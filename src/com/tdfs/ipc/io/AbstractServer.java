package com.tdfs.ipc.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.tdfs.fs.chunknode.ChunkNode;
import com.tdfs.interfaces.ipc.IPCServer;
import com.tdfs.ipc.element.DataPacket;


public abstract class AbstractServer implements IPCServer {
	
	protected ServerSocket providerSocket = null;
	protected Socket connection = null;
	private ObjectInputStream incomingData;	
	
	
	private static Logger logger = Logger.getLogger(AbstractServer.class);
	
	@Override
	public void startServer(InetAddress host, int port) {
		try {
			providerSocket = new ServerSocket();
			providerSocket.bind(new InetSocketAddress(host, port));
		} catch (IOException e) {
			// TODO ERROR HANDLING AND LOGGING
			//e.printStackTrace();
			logger.error("Error occurred in binding the server socket", e);
			
		}
		
				
	}


	@Override
	public void stopServer(InetAddress host, int port) {
		
		try {
			incomingData.close();
			providerSocket.close();
		} catch (IOException e) {
			// TODO ERROR HANDLING AND LOGGING
			//e.printStackTrace();
			logger.error("Error occurred while closing the Sockets and Streams ", e);
		}
		
		
	}

		
	public abstract void registerEvent(Socket socket,DataPacket<?> dataPacket);
	
	
	
	@Override
	public void run() {
		try{
			
			while(true)
			{
				
				//System.out.println("WAITING FOR INCOMING CONNECTIONS..."+providerSocket.getLocalPort());
				logger.info("Waiting for Incoming Connections at "+providerSocket.getLocalSocketAddress());
				
				connection = providerSocket.accept();
				//System.out.println("REQUEST FROM --"+connection.getRemoteSocketAddress());
				logger.info("Request from "+connection.getRemoteSocketAddress());
				
				incomingData = new ObjectInputStream(connection.getInputStream());
				
				DataPacket<?> dataPacket = (DataPacket<?>) incomingData.readObject();
				
				registerEvent(connection, dataPacket);
																		
			}
			
		}
		catch(Exception e)
		{
			logger.error("Exception in Run method of AbstractServer", e);
		}
		finally{
			
			try {
					incomingData.close();
								
			} catch (IOException e) {
				logger.error("Exception occurred in closing the stream", e);
			}
		}
		
	}


	
	
}
