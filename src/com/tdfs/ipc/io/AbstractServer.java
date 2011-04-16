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

import com.tdfs.interfaces.ipc.IPCServer;
import com.tdfs.ipc.element.DataPacket;


public abstract class AbstractServer implements IPCServer {
	
	protected ServerSocket providerSocket = null;
	protected Socket connection = null;
	private ObjectInputStream incomingData;	
	protected ThreadPoolExecutor handlerExecutors;
	
	@Override
	public void startServer(InetAddress host, int port) {
		try {
			providerSocket = new ServerSocket();
			providerSocket.bind(new InetSocketAddress(host, port));
		} catch (IOException e) {
			// TODO ERROR HANDLING AND LOGGING
			e.printStackTrace();
		}
		handlerExecutors = 
			new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5, true));
				
	}


	@Override
	public void stopServer(InetAddress host, int port) {
		
		try {
			incomingData.close();
			providerSocket.close();
		} catch (IOException e) {
			// TODO ERROR HANDLING AND LOGGING
			e.printStackTrace();
		}
		
		
	}

		
	public abstract void registerEvent(Socket socket,DataPacket<?> dataPacket);
	
	
	
	@Override
	public void run() {
		try{
			
			while(true)
			{
				
				System.out.println("WAITING FOR INCOMING CONNECTIONS..."+providerSocket.getLocalPort());
				connection = providerSocket.accept();
				// Delegate to Handler
				System.out.println("REQUEST FROM --"+connection.getRemoteSocketAddress());
				incomingData = new ObjectInputStream(connection.getInputStream());
				DataPacket<?> dataPacket = (DataPacket<?>) incomingData.readObject();
				
				registerEvent(connection, dataPacket);
																		
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			
			/*try {
				if(incomingData.available() == 0)
				{
					incomingData.close();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
	}


	
	
}
