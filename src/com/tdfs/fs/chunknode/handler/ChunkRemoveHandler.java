package com.tdfs.fs.chunknode.handler;

import java.util.Observable;

import org.apache.log4j.Logger;

import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;

public class ChunkRemoveHandler extends AbstractEventListener {

	private static Logger logger = Logger.getLogger(ChunkRemoveHandler.class);
	
	@Override
	public void update(Observable arg0, Object arg1) {
		DataEvent event = (DataEvent) arg0;
		if(arg1 instanceof DataPacket<?>)
		{
			DataPacket<?> packet = (DataPacket<?>) arg1;
			if(packet.getPacketType() == PacketType.CHUNK_REMOVE)
			{
				if(packet.getData() instanceof String)
				{
					logger.debug("Chunk Removal started");
					String chunkName = (String) packet.getData();
					if(removeChunk(chunkName))
					{
						sendResponse(event.getEventSocket(),
								new DataPacket<String>(PacketType.ACKNOWLEDGEMENT, "Chunk "+chunkName+" removed from disk", 
										System.currentTimeMillis(), null));
					}
					else{
						sendResponse(event.getEventSocket(),
								new DataPacket<String>(PacketType.ERROR, "Chunk "+chunkName+" cannot be removed!!", 
										System.currentTimeMillis(), null));
					}
					
				}
			}
		}

	}
	
	
	private boolean removeChunk(String chunkName)
	{
		DiskPersistence<Chunk> chunkPersistence = new DiskPersistence<Chunk>();
		if(chunkPersistence.removeFileFromDisk(ResourceLoader.getChunkLocation()+chunkName))
		{
			return true;
		}
		
		return false;
	}

}
