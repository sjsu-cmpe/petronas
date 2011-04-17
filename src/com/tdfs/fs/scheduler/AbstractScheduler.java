package com.tdfs.fs.scheduler;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractScheduler extends TimerTask {

	Timer timer;
	
	public AbstractScheduler(long initialDelay,long scheduledDelay)
	{
		timer = new Timer();
		timer.schedule(this, initialDelay, scheduledDelay);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		startTimedJob();

	}
	
	public abstract void startTimedJob();
	

}
