package com.tdfs.fs.metanode.element;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tdfs.fs.chunknode.ChunkNode;
import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.util.ResourceLoader;


/**
 * @author       gisripa
 */
public class FSMetadata implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957663910403414461L;
	private Map<String,INode> iNodeMap;
	private Map<String,List<InetSocketAddress>> chunkReplicationMap;
	private Map<String,InetSocketAddress> chunkLocationMap;
	private List<InetSocketAddress> chunkNodeList;
	private Map<String,List<InetSocketAddress>> fileAccessLog;
	
	private static int roundRobinIndex = 0;
	/**
	 */
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
	 * The metadata consists of File's name, checksum and the linked list of chunks to be joined to get the file. <br/>
	 * This method is used to get the metadata for the existing file in the distributed file system.
	 * 
	 * @param fileName
	 * @return File metadata
	 */
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
				metadata.iNodeMap = new HashMap<String,INode>();
				metadata.chunkReplicationMap = new HashMap<String,List<InetSocketAddress>>();
				metadata.chunkLocationMap = new HashMap<String, InetSocketAddress>();
				metadata.chunkNodeList = new ArrayList<InetSocketAddress>();
				metadata.fileAccessLog = new HashMap<String, List<InetSocketAddress>>();
				initHomeDirectory();
					
			}
			
			
		}
	
		return metadata;
	}
	
	private static void initHomeDirectory()
	{
		INode homeDirectory = new INode("Home");
		homeDirectory.setDirectory(true);
		Dentry dentry = new Dentry("Home", null);
		homeDirectory.setDirectoryEntry(dentry);
		
		metadata.iNodeMap.put("Home", homeDirectory);
		
	}
	
	
	
	public INode getINode(String fileName)
	{
		return iNodeMap.get(fileName);
	}
	
	public void updateINodeMap(String fileName,INode file)
	{
		synchronized (iNodeMap) {
			if(!iNodeMap.containsKey(fileName))
			{
				iNodeMap.put(fileName, file);
			}
			else{
				// TODO: Implement comparable and update only when changed
				iNodeMap.put(fileName, file);
			}
		}
		
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
		synchronized(chunkNodeList)
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
		
		
	}
	
	/**
	 * Round robin way of accessing available chunk Nodes
	 * @return
	 */
	public InetSocketAddress getAvailableChunkNode()
	{
		if(roundRobinIndex < this.chunkNodeList.size())
		{
			
			return this.chunkNodeList.get(roundRobinIndex++);
			
		}
		else
		{
			roundRobinIndex = 0;
			if(roundRobinIndex < this.chunkNodeList.size())
			{
				return this.chunkNodeList.get(roundRobinIndex++);
				
			}
			else{
				return null;
			}
		}
		
	}
	
	public Iterator<InetSocketAddress> getChunkNodeListIterator()
	{
		return this.chunkNodeList.iterator();
	}
	
	public List<InetSocketAddress> getChunkNodeList()
	{
		return this.chunkNodeList;
	}
	
	// TODO: Heuristics to decide the nearest available chunk node.
	@Deprecated
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
		synchronized (chunkLocationMap) {
			if(this.chunkLocationMap != null)
			{
				this.chunkLocationMap.put(chunkName, chunkAddress);
			}
			else{
				//TODO: Only put the changed value.
				this.chunkLocationMap.put(chunkName, chunkAddress);
			}
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
	
		
	public Set<String> getFilesAccessed()
	{
		return this.fileAccessLog.keySet();
	}
	
	public List<InetSocketAddress> getNodesAccessingFile(String fileName)
	{
		return  this.fileAccessLog.get(fileName);
	}
	
	public void updateAccessLog(String fileName,InetSocketAddress chunkNodeAddress)
	{
		List<InetSocketAddress> chunkNodeAddressList = null;
		synchronized (this.fileAccessLog) {
			if(this.fileAccessLog.containsKey(fileName))
			{
				chunkNodeAddressList = this.fileAccessLog.get(fileName);
				chunkNodeAddressList.add(chunkNodeAddress);
			}
			else{
				chunkNodeAddressList = new ArrayList<InetSocketAddress>();
				chunkNodeAddressList.add(chunkNodeAddress);
			}
			this.fileAccessLog.put(fileName, chunkNodeAddressList);
		}
		
	}
	
	public void clearAccessLog()
	{
		synchronized (this.fileAccessLog) {
			this.fileAccessLog.clear();
		}
		
	}

	
}
