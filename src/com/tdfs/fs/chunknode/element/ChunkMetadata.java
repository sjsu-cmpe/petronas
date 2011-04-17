package com.tdfs.fs.chunknode.element;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.util.ResourceLoader;

public class ChunkMetadata implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4967762301835882441L;
	
	private static ChunkMetadata chunkMetadata = null;
	private Set<String> chunkList = null;
	
	private ChunkMetadata()
	{
		
	}
	
	public static ChunkMetadata getInstance()
	{
		if(chunkMetadata == null)
		{
			chunkMetadata = new ChunkMetadata();
			if((chunkMetadata.chunkList = loadChunkListFromDisk()) == null)
			{
				System.out.println("Chunk Metadata not found.. creating new instance");
				chunkMetadata.chunkList = new HashSet<String>();
			}
			
		}
		
		return chunkMetadata;
	}
	
	
	
	public void add(String chunkName)
	{
		this.chunkList.add(chunkName);
	}
	
	public Set<String> getChunkList()
	{
		return this.chunkList;
	}
	
	private static Set<String> loadChunkListFromDisk()
	{
		DiskPersistence<Set<String>> diskPersistence = new DiskPersistence<Set<String>>();
		
		return diskPersistence.readObjectFromDisk(ResourceLoader.getChunkLocation()+"chunk_list");
		
		
	}
	
	
	public Iterator<String> getChunkIterator()
	{
		Iterator<String> chunkListIterator = this.chunkList.iterator();
		return chunkListIterator;
	}

}
