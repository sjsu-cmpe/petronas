package com.tdfs.fs;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractScheduler extends TimerTask {

	Timer timer;
	
	public AbstractScheduler()
	{
		timer = new Timer();
		timer.schedule(this, 1000, 1000);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		startTimedJob();

	}
	
	public abstract void startTimedJob();
	

}
