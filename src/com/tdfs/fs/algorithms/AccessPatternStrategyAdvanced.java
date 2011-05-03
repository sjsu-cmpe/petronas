package com.tdfs.fs.algorithms;

import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.interfaces.common.AccessPatternStrategy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


public class AccessPatternStrategyAdvanced implements AccessPatternStrategy {

	private FSMetadata metadata = null;
	
	
	private static Logger logger = Logger.getLogger(AccessPatternStrategyAdvanced.class);
	
	@Override
	public InetSocketAddress getChunkNode(String fileName) {
		Map<InetSocketAddress,Integer> accessCountPerChunkNode = new HashMap<InetSocketAddress, Integer>();
		Entry<InetSocketAddress,Integer> maxCountEntry = null;
		metadata = FSMetadata.getInstance();
		
		List<InetSocketAddress> chunkNodeAddresses = metadata.getNodesAccessingFile(fileName);
		
		int totalTimesAccessed = chunkNodeAddresses.size();
		
		for(InetSocketAddress chunkNodeAddress: chunkNodeAddresses)
		{
			if(!accessCountPerChunkNode.containsKey(chunkNodeAddress))
			{
				accessCountPerChunkNode.put(chunkNodeAddress, 1);
			}
			else{
				accessCountPerChunkNode.put(chunkNodeAddress, accessCountPerChunkNode.get(chunkNodeAddress)+1);
			}
			
		}
		Iterator<Entry<InetSocketAddress,Integer>> accessCountIterator = accessCountPerChunkNode.entrySet().iterator();
		
		while(accessCountIterator.hasNext())
		{
			Entry<InetSocketAddress,Integer> accessCountEntry =  accessCountIterator.next();
			System.out.println(accessCountEntry.getKey()+"-->"+accessCountEntry.getValue());
			if(maxCountEntry == null)
			{
				maxCountEntry = accessCountEntry;
			}
			else{
				if(accessCountEntry.getValue() > maxCountEntry.getValue())
				{
					maxCountEntry = accessCountEntry;
				}
			}
		}
		
		logger.debug(maxCountEntry.getKey()+" Accessed -->"+maxCountEntry.getValue()+" times");
		float probabilityOfAccess = (float) maxCountEntry.getValue()/totalTimesAccessed;
		logger.debug(probabilityOfAccess+" is the probability");
		if(probabilityOfAccess > 0.8)
		{
			return maxCountEntry.getKey();
		}
		
		return null;
		
	}

}
