package com.tdfs.fs.algorithms;

import java.net.InetSocketAddress;

import com.tdfs.interfaces.common.AccessPatternStrategy;

public class AccessPattern {
	
	private AccessPatternStrategy accessPatternStrategy = null;
	
	public InetSocketAddress getChunkNode(String fileName)
	{
		return accessPatternStrategy.getChunkNode(fileName);
	}
	
	public void setAccessPatternStrategy(AccessPatternStrategy accessPatternStrategy)
	{
		this.accessPatternStrategy = accessPatternStrategy;
	}
	
	public AccessPatternStrategy getAccessPatternStrategy()
	{
		return this.accessPatternStrategy;
	}

}
