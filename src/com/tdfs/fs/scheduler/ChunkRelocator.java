package com.tdfs.fs.scheduler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.tdfs.fs.algorithms.AccessPattern;
import com.tdfs.fs.algorithms.AccessPatternStrategyAdvanced;
import com.tdfs.fs.algorithms.AccessPatternStrategyLRU;
import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.metanode.MetaClient;
import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.io.AbstractClient;

public class ChunkRelocator extends AbstractScheduler {

	private FSMetadata metadata = null;
	private AbstractClient relocationClient = null;
	private static Logger logger = Logger.getLogger(ChunkRelocator.class);
	
	public ChunkRelocator(long initialDelay, long scheduledDelay) {
		super(initialDelay, scheduledDelay);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startTimedJob() {
		metadata = FSMetadata.getInstance();
		
		//TODO: Update access pattern call
		AccessPattern accessPattern = new AccessPattern();
		accessPattern.setAccessPatternStrategy(new AccessPatternStrategyAdvanced());
		
		Iterator<String> filesAccessed = metadata.getFilesAccessed().iterator();
		while(filesAccessed.hasNext())
		{
			String fileName = filesAccessed.next();
			relocateChunks(fileName, accessPattern.getChunkNode(fileName));
		}
		
	}
	
	private boolean relocateChunks(String fileName,InetSocketAddress chunkNodeAddress)
	{
		Iterator<String> chunkListIterator = null;
		boolean isRelocated = false;
		ExecutorService executorService = Executors.newCachedThreadPool();
		Set<Future<DataPacket<?>>> responseSet = new HashSet<Future<DataPacket<?>>>();
		try{
			if(chunkNodeAddress!=null)
			{
				chunkListIterator = metadata.getINode(fileName).getChunkList().iterator();
				while(chunkListIterator.hasNext())
				{
					String chunkName = chunkListIterator.next();
					InetSocketAddress srcChunkNodeAddress = metadata.getChunkNode(chunkName); 
					if(!chunkNodeAddress.equals(srcChunkNodeAddress))
					{
						// Relocate only when the chunkNode is different from the node where chunks are currently present
						logger.debug("Chunk "+chunkName+" relocation "+srcChunkNodeAddress+"-->"+chunkNodeAddress);
						relocationClient = new MetaClient(srcChunkNodeAddress.getAddress(),
								srcChunkNodeAddress.getPort(), 
								new DataPacket<String>(PacketType.CHUNK_READ, chunkName, System.currentTimeMillis(), null));
						if(relocationClient.initiateConnection())
						{
							responseSet.add(executorService.submit(relocationClient));
						}
						//relocationClient.finishConnection();
						relocationClient = new MetaClient(srcChunkNodeAddress.getAddress(),
								srcChunkNodeAddress.getPort(), 
								new DataPacket<String>(PacketType.CHUNK_REMOVE, chunkName, System.currentTimeMillis(), null)); 
						
						if(relocationClient.initiateConnection())
						{
							Future<DataPacket<?>> acknowledgement = executorService.submit(relocationClient);
							logger.info(acknowledgement.get().getData());
						}
						//relocationClient.finishConnection();
					}
				}
				for(Future<DataPacket<?>> response:responseSet)
				{
					Chunk chunk = (Chunk) response.get().getData();
					relocationClient = new MetaClient(chunkNodeAddress.getAddress(),
							chunkNodeAddress.getPort(), 
							new DataPacket<Chunk>(PacketType.CHUNK_READ, chunk, System.currentTimeMillis(), null));
					if(relocationClient.initiateConnection())
					{
						Future<DataPacket<?>> acknowledgement = executorService.submit(relocationClient);
						logger.debug(acknowledgement.get().getData());
						logger.info(chunk.getChunkFileName()+" Relocated");
						metadata.updateChunkLocationMap(chunk.getChunkFileName(), chunkNodeAddress);
						isRelocated = true;
						
						
					}
					//relocationClient.finishConnection();
					
				}
				
				
			}
		}
		catch(Exception e)
		{
			
		}
		
		
		
		return isRelocated;
	}

}
