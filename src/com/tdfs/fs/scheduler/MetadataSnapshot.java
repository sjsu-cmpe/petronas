package com.tdfs.fs.scheduler;

import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.fs.util.ResourceLoader;

public class MetadataSnapshot extends AbstractScheduler {

	public MetadataSnapshot(long initialDelay,long scheduledDelay)
	{
		super(initialDelay, scheduledDelay);
	}
	
	@Override
	public void startTimedJob() {
		
		persistMetadataSnapshot();
		

	}
	
	public void persistMetadataSnapshot()
	{
		DiskPersistence<FSMetadata> diskPersistence = new DiskPersistence<FSMetadata>();
		diskPersistence.writeObjectToDisk(FSMetadata.getInstance(), ResourceLoader.getMetadataLocation());
	}

}
