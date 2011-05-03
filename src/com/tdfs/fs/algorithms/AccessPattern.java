package com.tdfs.fs.algorithms;

import java.net.InetSocketAddress;

import com.tdfs.interfaces.common.AccessPatternStrategy;

/**
 * @author  gisripa
 */
public class AccessPattern {
	
	/**
	 */
	private AccessPatternStrategy accessPatternStrategy = null;
	
	public InetSocketAddress getChunkNode(String fileName)
	{
		return accessPatternStrategy.getChunkNode(fileName);
	}
	
	/** 
	 * @param accessPatternStrategy
	 */
	public void setAccessPatternStrategy(AccessPatternStrategy accessPatternStrategy)
	{
		this.accessPatternStrategy = accessPatternStrategy;
	}
	
	/** 
	 * @return
	 */
	public AccessPatternStrategy getAccessPatternStrategy()
	{
		return accessPatternStrategy;
	}

}
