package com.tdfs.fs.algorithms;

import java.net.InetSocketAddress;
import java.util.List;

import com.tdfs.interfaces.common.ChunkDistributionStrategy;

public class ChunkDistribution {
	
	private ChunkDistributionStrategy chunkDistribution = null;
	
	public ChunkDistributionStrategy getChunkDistribution() {
		return chunkDistribution;
	}

	public void setChunkDistribution(ChunkDistributionStrategy chunkDistribution) {
		this.chunkDistribution = chunkDistribution;
	}

	public List<InetSocketAddress> getChunkNodesForReplication()
	{
		return chunkDistribution.getChunkNodesForReplication();
	}

}
