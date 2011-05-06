package com.tdfs.fs.util;

public class SchedulerConfig {
	
	private String schedulerName;
	private long initialDelay;
	private long repeatPeriod;
	
	
	public String getSchedulerName() {
		return schedulerName;
	}
	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}
	public long getInitialDelay() {
		return initialDelay;
	}
	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}
	public long getRepeatPeriod() {
		return repeatPeriod;
	}
	public void setRepeatPeriod(long repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}
	
	
	

}
