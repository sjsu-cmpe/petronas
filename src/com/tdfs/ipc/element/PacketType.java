package com.tdfs.ipc.element;

/**
 * @author       gisripa
 */
public enum PacketType {
	/**
	 */
	MESSAGE("Message",1),
	/**
	 */
	ACKNOWLEDGEMENT("Acknowledgement of message",2),
	/**
	 */
	PING("Heart beat Ping",3),
	/**
	 */
	FILE_WRITE("Request to Write File",4),
	/**
	 */
	FILE_READ("Requet to Read File",5),
	/**
	 */
	CHUNK_READ("Read chunks from disk",6),
	/**
	 */
	CHUNK_WRITE("Write chunks to disk",7),
	/**
	 */
	CHUNK_MAP("Chunks map from MetaNode",8),
	/**
	 */
	CHUNK_DATA("File data from chunkNode",9),
	/**
	 */
	CHUNKNODE_REGISTER("First packet from chunknode for registration",10),
	/**
	 */
	ERROR("Error",19);
	
	private int packetType;
	private String packetDescription;
	
	
	PacketType(String packetDescription,int packetType)
	{
		this.packetType = packetType;
		this.packetDescription = packetDescription;
	}
	
	public String packetDescription()
	{
		return this.packetDescription;
	}
	
	public int packetType()
	{
		return this.packetType;
	}
	
	
	
	@Override
	public String toString()
	{
		return this.packetDescription;
	}
	

}
