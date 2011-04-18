package com.tdfs.ipc.element;

import java.net.InetSocketAddress;


public class FileWriteDataPacket extends DataPacket<byte[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -414293562856223720L;
	private String fileName;
	private long checkSum;
	private int packetNumber;
	private boolean isLastPacket;
	private boolean isDirectory;
	
	
	public String getFileName() {
		return fileName;
	}

	public long getCheckSum() {
		return checkSum;
	}
	
	public int getPacketSerialNo()
	{
		return this.packetNumber;
	}
	
	public boolean isLastPacket() {
		return isLastPacket;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public FileWriteDataPacket(PacketType packetType, byte[] data, long timeStamp,
			InetSocketAddress localChunkNodeInfo,String fileName,long checkSum,int packetNumber,boolean isLastPacket,boolean isDirectory) {
		super(packetType, data, timeStamp, localChunkNodeInfo);
		this.fileName = fileName;
		this.checkSum = checkSum;
		this.packetNumber = packetNumber;
		this.isLastPacket = isLastPacket;
		this.isDirectory = isDirectory;
		
	}
	
	
	
	

}
