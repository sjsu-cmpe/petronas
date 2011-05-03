package com.tdfs.fs.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;



/**
 * @author       gisripa
 */
public class ResourceLoader {
	
	/**
	 */
	ResourceLoader resourceLoader = new ResourceLoader();
	/**
	 */
	private static final int BLOCK_SIZE = 4*1024*1024;
	private static final String BLOCK_LOCATION = "/home/gisripa/Project/DFS/chunknode2/";
	/**
	 */
	private static final String METADATA_LOCATION = "/home/gisripa/Project/DFS/metanode/metafile";
	private static final String TEMP_LOCATION = "/home/gisripa/Project/DFS/client/";
	
	
	private ResourceLoader()
	{
		
	}
	
	/**
	 * @return
	 */
	public static int getBlockSize()
	{
		return BLOCK_SIZE;
	}
	
	/**
	 * @return
	 */
	public static String getMetadataLocation()
	{
		return METADATA_LOCATION;
	}

	public static String getChunkLocation()
	{
		return BLOCK_LOCATION;
	}
	
	public static String getClientTempLocation()
	{
		return TEMP_LOCATION;
	}
	
	public static InetSocketAddress getLocalChunkNodeAddress()
	{
		try {
			return new InetSocketAddress(InetAddress.getByName("localhost"), 9192);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
