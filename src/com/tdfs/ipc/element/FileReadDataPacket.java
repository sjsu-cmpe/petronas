package com.tdfs.ipc.element;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Map;

import com.tdfs.fs.metanode.element.Dentry;

//TODO: Change the class variables depending on the FILE metadata
/**
 * @author     gisripa
 */
public class FileReadDataPacket extends DataPacket<Map<String,InetSocketAddress>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 */
	private String fileName;
	/**
	 */
	private long checkSum;
	private LinkedList<String> chunksListInOrder;
	/**
	 */
	private boolean isDirectory;
	/**
	 */
	private Dentry directoryEntry;
	
	/**
	 * @return
	 */
	public String getFileName()
	{
		return this.fileName;
	}
	
	/**
	 * @return
	 */
	public long getCheckSum()
	{
		return this.checkSum;
	}
	
	public LinkedList<String> getChunksList()
	{
		return this.chunksListInOrder;
	}
	
	/**
	 * @return
	 */
	public boolean isDirectory() {
		return isDirectory;
	}
	
	/**
	 * @return
	 */
	public Dentry getDirectoryEntry() {
		return directoryEntry;
	}
	
	public FileReadDataPacket(PacketType packetType,
			Map<String, InetSocketAddress> data, long timeStamp,
			InetSocketAddress inetSocketAddress,String fileName,
			long checkSum,LinkedList<String> chunksListInOrder,boolean isDirectory,Dentry directoryEntry) 
	{
		super(packetType, data, timeStamp, inetSocketAddress);
		this.fileName = fileName;
		this.checkSum = checkSum;
		this.chunksListInOrder = chunksListInOrder;
		this.isDirectory = isDirectory;
		this.directoryEntry = directoryEntry;
		
	}

	

	

}
