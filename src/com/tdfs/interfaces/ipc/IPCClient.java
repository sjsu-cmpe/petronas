package com.tdfs.interfaces.ipc;

import java.util.concurrent.Callable;

import com.tdfs.ipc.element.DataPacket;


public interface IPCClient extends Callable<DataPacket<?>> {
	
	public void initiateConnection();
	
	public void finishConnection();

}
