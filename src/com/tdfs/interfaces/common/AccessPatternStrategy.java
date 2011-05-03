package com.tdfs.interfaces.common;

import java.net.InetSocketAddress;

public interface AccessPatternStrategy {
	
	public InetSocketAddress getChunkNode(String fileName);

}
