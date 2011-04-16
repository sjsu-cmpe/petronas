package com.tdfs.fs.chunknode;

import java.net.InetAddress;

import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.io.AbstractClient;

public class ChunkClient extends AbstractClient {

	private DataPacket<?> dataPacket = null;
	
	ChunkClient(InetAddress host, int port, DataPacket<?> dataPacket) {
		super(host, port);
		this.dataPacket = dataPacket;
	}
	
	
	@Override
	public DataPacket<?> call() throws Exception {
		// TODO Auto-generated method stub

		this.sendMessage(this.dataPacket);
		
		DataPacket<String> incomingPacket = (DataPacket<String>) this.responseHandler();
		if(incomingPacket!=null)
		{
			System.out.println(incomingPacket.getData());
		}
		return incomingPacket;
	}

}
