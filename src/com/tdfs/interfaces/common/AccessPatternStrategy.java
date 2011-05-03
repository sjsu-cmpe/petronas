package com.tdfs.interfaces.common;

import java.net.InetSocketAddress;
import com.tdfs.fs.algorithms.AccessPattern;

public interface AccessPatternStrategy {
	
	public InetSocketAddress getChunkNode(String fileName);

}
