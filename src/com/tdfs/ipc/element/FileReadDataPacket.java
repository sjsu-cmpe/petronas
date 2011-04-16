package com.tdfs.ipc.element;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Map;

//TODO: Change the class variables depending on the FILE metadata
public class FileReadDataPacket extends DataPacket<Map<String,InetSocketAddress>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileName;
	private long checkSum;
	private LinkedList<String> chunksListInOrder;
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public long getCheckSum()
	{
		return this.checkSum;
	}
	
	public LinkedList<String> getChunksList()
	{
		return this.chunksListInOrder;
	}
	
	public FileReadDataPacket(PacketType packetType,
			Map<String, InetSocketAddress> data, long timeStamp,
			InetSocketAddress inetSocketAddress,String fileName,
			long checkSum,LinkedList<String> chunksListInOrder) 
	{
		super(packetType, data, timeStamp, inetSocketAddress);
		this.fileName = fileName;
		this.checkSum = checkSum;
		this.chunksListInOrder = chunksListInOrder;
		
	}

}
