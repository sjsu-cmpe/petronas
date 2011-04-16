package com.tdfs.fs.metanode;

import java.net.InetAddress;

import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.io.AbstractClient;

public class MetaClient extends AbstractClient {

	private DataPacket<?> dataPacket = null;
	
	public MetaClient(InetAddress host, int port, DataPacket<?> dataPacket) {
		super(host, port);
		this.dataPacket = dataPacket;
	}

	@Override
	public DataPacket<?> call() throws Exception {
		this.sendMessage(this.dataPacket);
		return this.responseHandler();
	}

}
