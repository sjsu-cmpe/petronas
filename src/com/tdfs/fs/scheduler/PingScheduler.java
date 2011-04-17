package com.tdfs.fs.scheduler;

public class PingScheduler extends AbstractScheduler {

	public PingScheduler(long initialDelay, long scheduledDelay) {
		super(initialDelay, scheduledDelay);
		
	}

	@Override
	public void startTimedJob() {
		System.out.println("Timed and started");

	}
	
	public static void main(String...args)
	{
		AbstractScheduler scheduler = new PingScheduler(1000, 1000);
	}

}
