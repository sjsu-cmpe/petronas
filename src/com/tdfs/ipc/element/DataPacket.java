package com.tdfs.ipc.element;

import java.io.Serializable;
import java.net.InetSocketAddress;



/**
 * @author     gisripa
 */
public class DataPacket<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3834531167588712868L;
	/**
	 */
	private PacketType packetType;
	/**
	 */
	private T data;
	/**
	 */
	private long timeStamp;
	private InetSocketAddress localChunkNodeInfo;
	
	public DataPacket(PacketType packetType,T data,long timeStamp,InetSocketAddress inetSocketAddress)
	{
		this.packetType = packetType;
		this.data = data;
		this.timeStamp = timeStamp;
		this.localChunkNodeInfo = inetSocketAddress;
		
	}
	
	/**
	 * @return
	 */
	public PacketType getPacketType() {
		return packetType;
	}
	/**
	 * @return
	 */
	public T getData() {
		return data;
	}
	/**
	 * @return
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	public InetSocketAddress getChunkNodeInfo()
	{
		return this.localChunkNodeInfo;
	}
	
}
