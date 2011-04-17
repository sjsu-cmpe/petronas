package com.tdfs.fs.metanode.element;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.util.ResourceLoader;


public class FSMetadata implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957663910403414461L;
	private Map<String,File> fileMap;
	private Map<String,List<InetSocketAddress>> chunkReplicationMap;
	private Map<String,InetSocketAddress> chunkLocationMap;
	private List<InetSocketAddress> chunkNodeList;
	
	private static FSMetadata metadata = null;
	
	private FSMetadata()
	{
		
	}
	
	private static FSMetadata loadMetadataFromDisk()
	{
		DiskPersistence<FSMetadata> diskPersistence = new DiskPersistence<FSMetadata>();
		return diskPersistence.readObjectFromDisk(ResourceLoader.getMetadataLocation());
	}
	
	/**
	 * Singleton for Metadata of the file system.
	 * @return
	 */
	public static FSMetadata getInstance()
	{
		if(metadata == null)
		{
			if((metadata = loadMetadataFromDisk()) == null)
			{
				//TODO: Remove this
				System.out.println("Metadata not found - creating new metadata Instance");
				metadata = new FSMetadata();
				metadata.fileMap = new HashMap<String,File>();
				metadata.chunkReplicationMap = new HashMap<String,List<InetSocketAddress>>();
				metadata.chunkLocationMap = new HashMap<String, InetSocketAddress>();
				metadata.chunkNodeList = new ArrayList<InetSocketAddress>();
					
			}
		}
	
		return metadata;
	}
	
	/**
	 * The metadata consists of File's name, checksum and the linked list of chunks to be joined to get the file. <br/>
	 * This method is used to get the metadata for the existing file in the distributed file system.
	 * 
	 * @param fileName
	 * @return File metadata
	 */
	public File getFileMetadata(String fileName)
	{
		return fileMap.get(fileName);
	}
	
	public void updateFileMetadata(String fileName,File file)
	{
		if(!fileMap.containsKey(fileName))
		{
			fileMap.put(fileName, file);
		}
		else{
			// TODO: Implement comparable and update only when changed
			fileMap.put(fileName, file);
		}
	}
	
	public void updateChunkLocation(String blockName,List<InetSocketAddress> blockLocationList)
	{
		//TODO: Implement block location update strategy
		chunkReplicationMap.put(blockName, blockLocationList);
	}
	
	public void updateChunkReplicationMap(String chunkName,InetSocketAddress chunkNodeAddress)
	{
		if(this.chunkReplicationMap != null)
		{
			List<InetSocketAddress> chunkLocationList = null;
			if(chunkReplicationMap.containsKey(chunkName))
			{
				chunkLocationList = chunkReplicationMap.get(chunkName);
				if(!chunkLocationList.contains(chunkNodeAddress))
				{
					chunkLocationList.add(chunkNodeAddress);
				}
				this.chunkReplicationMap.put(chunkName, chunkLocationList);
			}
			else
			{
				chunkLocationList = new ArrayList<InetSocketAddress>();
				chunkLocationList.add(chunkNodeAddress);
				this.chunkReplicationMap.put(chunkName, chunkLocationList);
			}
		}
	}
	
	public void updateChunkNodeList(InetSocketAddress hostAddress,boolean isAlive)
	{
		if(isAlive)
		{
			if(!chunkNodeList.contains(hostAddress))
			{
				chunkNodeList.add(hostAddress);
			}
		}
		else{
			chunkNodeList.remove(chunkNodeList);
		}
		
	}
	
	public InetSocketAddress getAvailableChunkNode()
	{
		return chunkNodeList.get(0);
	}
	
	
	// TODO: Heuristics to decide the nearest available chunk node.
	public InetSocketAddress getChunkNode(InetSocketAddress sourceNode)
	{
		return chunkNodeList.get(0);
	}
	
	//TODO: Fix the algorithm to retrieve available node for chunk
	public InetSocketAddress getChunkNode(String chunkName)
	{
		
		InetSocketAddress availableNode = null;
 		if(chunkLocationMap != null)
		{
			if(chunkLocationMap.containsKey(chunkName))
			{
				availableNode = chunkLocationMap.get(chunkName);
			}
		}
		
		return availableNode;
		
	}
	
	public void updateChunkLocationMap(String chunkName,InetSocketAddress chunkAddress)
	{
		if(this.chunkLocationMap != null)
		{
			this.chunkLocationMap.put(chunkName, chunkAddress);
		}
	}
	
	
	public void validateChunkLocations(Set<String> chunkNames,InetSocketAddress chunkNodeAddress)
	{
		for(String chunkName: chunkNames)
		{
			updateChunkLocationMap(chunkName, chunkNodeAddress);
			updateChunkReplicationMap(chunkName, chunkNodeAddress);
		}
	}
	

	
}
