package com.tdfs.fs.chunknode;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tdfs.fs.chunknode.element.ChunkMetadata;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.scheduler.AbstractScheduler;
import com.tdfs.fs.scheduler.ChunkdataSnapshot;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.DataEvent;
import com.tdfs.ipc.io.AbstractClient;
import com.tdfs.ipc.io.AbstractServer;

public class ChunkNode extends AbstractServer{
	
	private DataEvent dataEvent = null;
	private ChunkWriteHandler chunkWriteHandler = null;
	private ChunkReadHandler chunkReadHandler = null;
	private ChunkMetadata chunkInfo = null;
	private AbstractScheduler scheduler = null;
	
	public ChunkNode(InetAddress host, int port)
	{
		super.startServer(host, port);
		initChunkNode();		
	}
	
	
	
	private void initChunkNode()
	{
		chunkInfo = ChunkMetadata.getInstance();
		registerEventHandlers();
		registerWithMetaNode();
		scheduler = new ChunkdataSnapshot(3000, 3000);
	}
	
	private void registerEventHandlers()
	{
		System.out.println("Registering event handlers");
		this.dataEvent = new DataEvent();
		this.chunkWriteHandler = new ChunkWriteHandler();
		this.chunkReadHandler = new ChunkReadHandler();
		this.dataEvent.addObserver(this.chunkWriteHandler);
		this.dataEvent.addObserver(chunkReadHandler);
	}
	
		
	@Override
	public void registerEvent(Socket socket,DataPacket<?> dataPacket) {
		
		this.dataEvent.setDataPacket(socket, dataPacket);
		
	}
	
	
	private void registerWithMetaNode()
	{
	
		// TODO: First register with meta node and then send chunks
		System.out.println("Registering Chunk Node with Meta Node...");
		try{
			ExecutorService pool = Executors.newFixedThreadPool(2);
			AbstractClient client = new ChunkClient(InetAddress.getByName("localhost"), 9090, 
			new DataPacket<Set<String>>(PacketType.CHUNKNODE_REGISTER, chunkInfo.getChunkList(), System.currentTimeMillis(),
					ResourceLoader.getLocalChunkNodeAddress()));
			client.initiateConnection();
			Future<DataPacket<?>> future = pool.submit(client);
			
			if(future.get().getPacketType() == PacketType.ACKNOWLEDGEMENT)
			{
				System.out.println("Block list sent to metanode");
			}
			
		}
		catch(UnknownHostException uhe)
		{
			//TODO: handle properly
			uhe.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args)
	{
		try{
			
			ChunkNode server = new ChunkNode(InetAddress.getByName("localhost"), 9191);
			new Thread((AbstractServer)server).start();
						
		}
		catch(Exception e)
		{
			//TODO: Error Handling and Loggin
			e.printStackTrace();
		}
	}

	

}
