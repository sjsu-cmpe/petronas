package com.tdfs.interfaces.ipc;

import java.net.InetAddress;

public interface IPCServer extends Runnable{
	
	public void startServer(InetAddress host,int port);
			
	public void stopServer(InetAddress host,int port);

}
