package com.tdfs.interfaces.common;

import java.net.InetSocketAddress;
import java.util.List;

public interface ChunkDistributionStrategy {
	
	public List<InetSocketAddress> getChunkNodesForReplication();

}
