package com.tdfs.fs.scheduler;

import com.tdfs.fs.algorithms.ChunkDistribution;
import com.tdfs.fs.algorithms.ChunkDistributionStrategyImpl;

public class ChunkReplicator extends AbstractScheduler {

	public ChunkReplicator(long initialDelay, long scheduledDelay) {
		super(initialDelay, scheduledDelay);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startTimedJob() {
		// TODO Auto-generated method stub
		ChunkDistribution chunkDistribution = new ChunkDistribution();
		chunkDistribution.setChunkDistribution(new ChunkDistributionStrategyImpl());
		
		chunkDistribution.getChunkNodesForReplication();

	}

}
