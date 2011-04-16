package com.tdfs.fs.metanode;

import java.net.Socket;
import java.util.Observable;
import java.util.Set;

import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;

public class SocketEventHandler extends AbstractEventListener {

	private Socket responseSocket = null;
	private FSMetadata metadata = null;
	private DataPacket<?> dataPacket = null;
	@Override
	public void update(Observable arg0, Object arg1) {
		DataEvent event = (DataEvent) arg0;
		if(arg1 instanceof DataPacket<?>)
		{
			this.dataPacket = (DataPacket<?>) arg1;
			switch(dataPacket.getPacketType())
			{
			case CHUNKNODE_REGISTER:
				this.responseSocket = event.getEventSocket();
				registerChunkNode();
				updateChunkLocationList();
				sendAcknowledgement();
				break;
			default:
				
			}
		}

	}
	
	private void registerChunkNode()
	{
		metadata = FSMetadata.getInstance();
		metadata.updateChunkNodeList(this.dataPacket.getChunkNodeInfo(), true);
		
	}
	
	@SuppressWarnings("unchecked")
	private void updateChunkLocationList()
	{
		if(dataPacket.getData() instanceof Set<?>)
		{
			metadata.validateChunkLocations((Set<String>) dataPacket.getData(), dataPacket.getChunkNodeInfo());
		}
		
	}
	
	private void sendAcknowledgement()
	{
		sendResponse(responseSocket, 
				new DataPacket<String>(PacketType.ACKNOWLEDGEMENT, 
						"Chunk Node registered", System.currentTimeMillis(), null));
	}

}
