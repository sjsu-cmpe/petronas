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

import org.apache.log4j.Logger;

import com.tdfs.fs.chunknode.element.ChunkMetadata;
import com.tdfs.fs.chunknode.handler.ChunkReadHandler;
import com.tdfs.fs.chunknode.handler.ChunkRemoveHandler;
import com.tdfs.fs.chunknode.handler.ChunkWriteHandler;
import com.tdfs.fs.chunknode.handler.PingHandler;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.scheduler.AbstractScheduler;
import com.tdfs.fs.scheduler.ChunkdataSnapshot;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.DataEvent;
import com.tdfs.ipc.io.AbstractClient;
import com.tdfs.ipc.io.AbstractServer;

/**
 * @author       gisripa
 */
public class ChunkNode extends AbstractServer{
	
	/**
	 */
	private DataEvent dataEvent = null;
	/**
	 */
	private ChunkWriteHandler chunkWriteHandler = null;
	/**
	 */
	private ChunkReadHandler chunkReadHandler = null;
	
	private ChunkRemoveHandler chunkRemoveHandler = null;
	
	private PingHandler pingHandler = null;
	/**
	 */
	private ChunkMetadata chunkInfo = null;
	/**
	 */
	private AbstractScheduler scheduler = null;
	
	private static Logger logger = Logger.getLogger(ChunkNode.class);
	
	// System.out
	public ChunkNode(InetAddress host, int port)
	{
		super.startServer(host, port);
		initChunkNode();		
	}
	
	
	
	private void initChunkNode()
	{
		chunkInfo = ChunkMetadata.getInstance();
		registerEventHandlers();
		try {
			registerWithMetaNode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("registration with MetaNode failed",e);
			System.exit(0);
		}
		scheduler = new ChunkdataSnapshot(3000, 3000);
	}
	
	private void registerEventHandlers()
	{
		logger.debug("Registering Event Handlers for ChunkNode...");
		this.dataEvent = new DataEvent();
		this.chunkWriteHandler = new ChunkWriteHandler();
		this.chunkReadHandler = new ChunkReadHandler();
		this.chunkRemoveHandler = new ChunkRemoveHandler();
		this.pingHandler = new PingHandler();
		this.dataEvent.addObserver(this.chunkWriteHandler);
		this.dataEvent.addObserver(chunkReadHandler);
		this.dataEvent.addObserver(pingHandler);
		this.dataEvent.addObserver(chunkRemoveHandler);
	}
	
		
	@Override
	public void registerEvent(Socket socket,DataPacket<?> dataPacket) {
		
		this.dataEvent.setDataPacket(socket, dataPacket);
		
	}
	
	
	private void registerWithMetaNode() throws Exception
	{
	
		// TODO: First register with meta node and then send chunks
		logger.info("Registering Chunk Node with Meta Node");
		
		try{
			ExecutorService pool = Executors.newFixedThreadPool(2);
			AbstractClient client = new ChunkClient(ResourceLoader.getMetaNodeAddress().getAddress(), ResourceLoader.getMetaNodeAddress().getPort(), 
			new DataPacket<Set<String>>(PacketType.CHUNKNODE_REGISTER, chunkInfo.getChunkList(), System.currentTimeMillis(),
					ResourceLoader.getLocalChunkNodeAddress()));
			if(!client.initiateConnection())
			{
				throw new Exception("Connection failed to connect to Metanode");
			}
			Future<DataPacket<?>> future = pool.submit(client);
			DataPacket<?> dataPacket = future.get();
			if(dataPacket != null)
			{
				if(dataPacket.getPacketType() == PacketType.ACKNOWLEDGEMENT)
				{
					logger.info("Acknowledgement From MetaNode -->"+dataPacket.getData());
				}
			}
			
			
		}
		catch(UnknownHostException uhe)
		{
			logger.error("Exception occurred in registering ChunkNode",uhe);
		} catch (InterruptedException e) {
			logger.error("Exception occurred in registering ChunkNode",e);
		} catch (ExecutionException e) {
			logger.error("Exception occurred in registering ChunkNode",e);
		}
		
		
	}
	
	public static void main(String[] args)
	{
		try{
			ResourceLoader.loadConfigurations();
			ChunkNode server = new ChunkNode(ResourceLoader.getLocalChunkNodeAddress().getAddress(),ResourceLoader.getLocalChunkNodeAddress().getPort());
			new Thread((AbstractServer)server).start();
						
		}
		catch(Exception e)
		{
			logger.error("Exception in starting ChunkNode",e);
		}
	}

	

}
