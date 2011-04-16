package com.tdfs.fs;

public class PingScheduler extends AbstractScheduler {

	@Override
	public void startTimedJob() {
		System.out.println("Timed and started");

	}
	
	public static void main(String...args)
	{
		AbstractScheduler scheduler = new PingScheduler();
	}

}
