package com.tdfs.fs.chunknode;

import java.util.Observable;

import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.chunknode.element.ChunkMetadata;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;

public class ChunkWriteHandler extends AbstractEventListener {

	private Chunk chunk = null;
	private ChunkMetadata chunkInfo = null;
	
	@Override
	public void update(Observable arg0, Object arg1) {
		
		//System.out.println("Encountered!!!");
		DataEvent event = (DataEvent)arg0;
		if(arg1 instanceof DataPacket<?>)
		{
			DataPacket<?> packet = (DataPacket<?>) arg1;
			if(packet.getData() instanceof Chunk)
			{
				this.chunk = (Chunk) packet.getData();
				writeChunk();
				sendResponse(event.getEventSocket(), 
						new DataPacket<String>(PacketType.ACKNOWLEDGEMENT, 
								"Chunk"+this.chunk.getChunkFileName()+ "persisted", System.currentTimeMillis(), null));
				chunkInfo = ChunkMetadata.getInstance();
				chunkInfo.add(chunk.getChunkFileName());
			}
			
			
		}
		
		

	}
	
	// TODO: Throw error back to requester
	private void writeChunk()
	{
		
		DiskPersistence<Chunk> chunkPersistence = new DiskPersistence<Chunk>();
		chunkPersistence.writeObjectToDisk(chunk, ResourceLoader.getChunkLocation()+chunk.getChunkFileName());
		

	}

}
