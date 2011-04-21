package com.tdfs.fs.metanode.handler;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;

import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.fs.metanode.element.INode;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.FileReadDataPacket;
import com.tdfs.ipc.element.FileWriteDataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;

/**
 * @author     gisripa
 */
public class FileReadHandler extends AbstractEventListener {

	/**
	 */
	private FSMetadata metadata = null;
	/**
	 */
	private DataPacket<?> dataPacket = null;
	/**
	 */
	private INode file = null;
	private Socket responseSocket = null;
	private Map<String, InetSocketAddress> chunkMap = null;
	String fileName;
	
	private static Logger logger = Logger.getLogger(FileReadHandler.class);
	
	public FileReadHandler()
	{
		metadata = FSMetadata.getInstance();
		chunkMap = new HashMap<String, InetSocketAddress>();
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		
		DataEvent event = (DataEvent) arg0;		
		if(arg1 instanceof DataPacket<?>)
		{
			if(((DataPacket<?>) arg1).getPacketType() == PacketType.FILE_READ)
			{
				dataPacket = (DataPacket<?>) arg1;
				fileName = (String) dataPacket.getData();
				getAvailableChunksList(fileName);
				responseSocket = event.getEventSocket();
				sendChunkMap();
			}
						
		}

	}
	
	
	//TODO: Validate the file Path before reading the File
	private void getAvailableChunksList(String fileName)
	{
		file = metadata.getINode(fileName);
		file.getBlockList();
		List<String> chunkNames = file.getBlockList();
		logger.debug("Chunk List Retrieved -->"+chunkNames.toString());
		for(String chunkName : chunkNames)
		{
			chunkMap.put(chunkName, metadata.getChunkNode(chunkName));
		}
	}
	
	private void sendChunkMap()
	{
		
		sendResponse(responseSocket, 
				new FileReadDataPacket(PacketType.CHUNK_MAP, 
						this.chunkMap, System.currentTimeMillis(), 
						null, file.getFileName(), file.getChecksum(),
						(LinkedList<String>) file.getBlockList(), false, file.getDirectoryEntry()));
		
	}

}
