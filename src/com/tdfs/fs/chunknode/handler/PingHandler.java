package com.tdfs.fs.chunknode.handler;

import java.util.Observable;

import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;

public class PingHandler extends AbstractEventListener {

	@Override
	public void update(Observable arg0, Object arg1) {
		DataEvent event = (DataEvent) arg0;
		
		if(arg1 instanceof DataPacket<?>)
		{
			DataPacket<?> dataPacket = (DataPacket<?>) arg1;
			if(dataPacket.getPacketType() == PacketType.PING)
			{
				if(dataPacket.getData() instanceof Boolean)
				{
					sendResponse(event.getEventSocket(), 
							new DataPacket<Boolean>(PacketType.PING, 
									(Boolean) dataPacket.getData(), System.currentTimeMillis(), ResourceLoader.getLocalChunkNodeAddress()));
				}
			}
			
		}

	}

}
