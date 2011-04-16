package com.tdfs.fs.metanode;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.io.AbstractDataHandler;
import com.tdfs.ipc.io.AbstractServer;

public class MetadataHandler extends AbstractDataHandler{

	public MetadataHandler(DataPacket<?> dataPacket, AbstractServer server)
	{
		super(dataPacket,server);
	}
	
	
	@Override
	public void run() {

		System.out.println("HANDLING REQUEST for ...");
		//TODO: What to do with data when handling
		List<String> blockList = (List<String>) dataPacket.getData();
		for(String blkName: blockList)
		{
			System.out.println(blkName);
		}
		
				
		try {
			sendResponse(null, new DataPacket<String>(PacketType.ACKNOWLEDGEMENT, "Received your data!!", System.currentTimeMillis(), 
					null));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
