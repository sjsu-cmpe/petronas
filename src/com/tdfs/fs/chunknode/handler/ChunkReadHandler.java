package com.tdfs.fs.chunknode.handler;

import java.util.Observable;

import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;

/**
 * @author       gisripa
 */
public class ChunkReadHandler extends AbstractEventListener {

	/**
	 */
	private Chunk chunk = null;
	
	@Override
	public void update(Observable arg0, Object arg1) {
		DataEvent event = (DataEvent) arg0;
		if(arg1 instanceof DataPacket<?>)
		{
			DataPacket<?> packet = (DataPacket<?>) arg1;
			if(packet.getPacketType() == PacketType.CHUNK_READ)
			{
				if(packet.getData() instanceof String)
				{
					String chunkName = (String) packet.getData();
					readChunk(chunkName);
					
					sendResponse(event.getEventSocket(),
							new DataPacket<Chunk>(PacketType.CHUNK_DATA, chunk, System.currentTimeMillis(), null));
				}
			}
			
			
		}

	}
	
	
	private void readChunk(String chunkName)
	{
		DiskPersistence<Chunk> chunkPersistence = new DiskPersistence<Chunk>();
		chunk = chunkPersistence.readObjectFromDisk(ResourceLoader.getChunkLocation()+chunkName);
	}

}
