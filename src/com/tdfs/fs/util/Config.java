package com.tdfs.fs.util;

import java.util.List;

public class Config {
	
	private String masterHostName;
	private int masterPort;
	private String masterStorageLocation;
	private String slaveHostName;
	private int slavePort;
	private String slaveStorageLocation;
	private List<SchedulerConfig> schedulersEnabled;
	private long chunkSize;
	
	
	public String getMasterHostName() {
		return masterHostName;
	}
	public void setMasterHostName(String masterHostName) {
		this.masterHostName = masterHostName;
	}
	public int getMasterPort() {
		return masterPort;
	}
	public void setMasterPort(int masterPort) {
		this.masterPort = masterPort;
	}
	public String getMasterStorageLocation() {
		return masterStorageLocation;
	}
	public void setMasterStorageLocation(String masterStorageLocation) {
		this.masterStorageLocation = masterStorageLocation;
	}
	public String getSlaveHostName() {
		return slaveHostName;
	}
	public void setSlaveHostName(String slaveHostName) {
		this.slaveHostName = slaveHostName;
	}
	public int getSlavePort() {
		return slavePort;
	}
	public void setSlavePort(int slavePort) {
		this.slavePort = slavePort;
	}
	public String getSlaveStorageLocation() {
		return slaveStorageLocation;
	}
	public void setSlaveStorageLocation(String slaveStorageLocation) {
		this.slaveStorageLocation = slaveStorageLocation;
	}
	public List<SchedulerConfig> getSchedulersEnabled() {
		return schedulersEnabled;
	}
	public void setSchedulersEnabled(List<SchedulerConfig> schedulersEnabled) {
		this.schedulersEnabled = schedulersEnabled;
	}
	public void setChunkSize(long chunkSize) {
		this.chunkSize = chunkSize;
	}
	public long getChunkSize() {
		return chunkSize;
	}
	
	
	

}
