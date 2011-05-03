package com.tdfs.fs.scheduler;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.tdfs.fs.metanode.MetaClient;
import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.io.AbstractClient;

public class PingScheduler extends AbstractScheduler {

	private FSMetadata metadata = null;
	private static Logger logger = Logger.getLogger(PingScheduler.class);
	private AbstractClient client = null;
	
	
	public PingScheduler(long initialDelay, long scheduledDelay) {
		super(initialDelay, scheduledDelay);
		
	}

	@Override
	public void startTimedJob() {
		
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		Set<Future<DataPacket<?>>> responseSet = new HashSet<Future<DataPacket<?>>>();
		metadata = FSMetadata.getInstance();
		try{
			Iterator<InetSocketAddress> chunkNodeAddresses = metadata.getChunkNodeListIterator();
			InetSocketAddress chunkNodeAddress = null;
			//for(InetSocketAddress chunkNodeAddress: metadata.getChunkNodeList())
			while(chunkNodeAddresses.hasNext())
			{
				chunkNodeAddress = chunkNodeAddresses.next();
				
								
				client = new MetaClient(chunkNodeAddress.getAddress(), chunkNodeAddress.getPort(), 
						new DataPacket<Boolean>(PacketType.PING, true, System.currentTimeMillis(), null));
				if(client.initiateConnection())
				{
					responseSet.add(executorService.submit(client));
				}
			
				else{
					logger.error("Chunk Node is down"+chunkNodeAddress);
					//metadata.updateChunkNodeList(chunkNodeAddress, false);
					chunkNodeAddresses.remove();
					
				}
				
				
			}
			
			for(Future<DataPacket<?>> response: responseSet)
			{
				DataPacket<Boolean> data = (DataPacket<Boolean>) response.get();
				if(data.getData())
				{
					//System.out.println("Chunk Node alive"+data.getChunkNodeInfo());
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Ping Request Failed");
			
		}

	}
	
	public static void main(String... args)
	{
		AbstractScheduler scheduler = new PingScheduler(1000, 1000);
	}

}
