package com.tdfs.fs.scheduler;

import java.util.Set;

import com.tdfs.fs.chunknode.element.ChunkMetadata;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.util.ResourceLoader;

public class ChunkdataSnapshot extends AbstractScheduler {

	public ChunkdataSnapshot(long initialDelay,long scheduledDelay)
	{
		super(initialDelay,scheduledDelay);
	}
	
	@Override
	public void startTimedJob() {
		persistChunkListSnapshot();

	}

	public void persistChunkListSnapshot()
	{
		DiskPersistence<Set<String>> diskPersistence = new DiskPersistence<Set<String>>();
		diskPersistence.writeObjectToDisk(ChunkMetadata.getInstance().getChunkList(), 
				ResourceLoader.getChunkLocation()+"chunk_list");
		
	}
}
