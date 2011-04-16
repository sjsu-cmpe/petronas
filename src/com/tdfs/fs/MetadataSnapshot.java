package com.tdfs.fs;

import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.fs.util.ResourceLoader;

public class MetadataSnapshot extends AbstractScheduler {

	@Override
	public void startTimedJob() {
		// TODO Auto-generated method stub
		

	}
	
	public void persistMetadataSnapshot()
	{
		DiskPersistence<FSMetadata> diskPersistence = new DiskPersistence<FSMetadata>();
		diskPersistence.writeObjectToDisk(FSMetadata.getInstance(), ResourceLoader.getMetadataLocation());
	}

}
