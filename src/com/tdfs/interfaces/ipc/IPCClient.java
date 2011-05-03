package com.tdfs.interfaces.ipc;

import java.util.concurrent.Callable;

import com.tdfs.ipc.element.DataPacket;


public interface IPCClient extends Callable<DataPacket<?>> {
	
	public boolean initiateConnection();
	
	public void finishConnection();

}
