package com.tdfs.fs.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;



/**
 * @author       gisripa
 */
public class ResourceLoader {
	
	/**
	 */
	ResourceLoader resourceLoader = new ResourceLoader();
	/**
	 */
	private static final int BLOCK_SIZE = 64*1024*1024;
	private static final String BLOCK_LOCATION = "/home/gisripa/Project/DFS/chunknode2/";
	/**
	 */
	private static final String METADATA_LOCATION = "/home/gisripa/Project/DFS/metanode/metafile";
	private static final String TEMP_LOCATION = "/home/gisripa/Project/DFS/client/";
	
	private static final String HOST_NAME = "192.168.1.4";
	
	private static final String SLAVE_HOST_NAME = "192.168.1.4";
	
	private static ConfigParser configParser = null;
	
	private static Config config = null;
	
	private static long chunkSize;
	private static String chunkStorageLocation;
	private static String metaStorageLocation;
	private static String tempLocation;
	private static String masterHostName;
	private static int masterPort;
	private static String slaveHostName;
	private static int slavePort;
	
	private List<SchedulerConfig> schedulersEnabled;
	
	
	private ResourceLoader()
	{
		
		
	}
	
	public static void loadConfigurations()
	{
		configParser = new ConfigParser();
		config = configParser.parseConfigurationFile();
		chunkSize = config.getChunkSize();
		chunkStorageLocation = config.getSlaveStorageLocation();
		metaStorageLocation = config.getMasterStorageLocation();
		masterHostName = config.getMasterHostName();
		masterPort = config.getMasterPort();
		slaveHostName = config.getSlaveHostName();
		slavePort = config.getSlavePort();
		
	}
	
	/**
	 * @return
	 */
	public static long getBlockSize()
	{
		return chunkSize;
	}
	
	/**
	 * @return
	 */
	public static String getMetadataLocation()
	{
		return metaStorageLocation;
	}

	public static String getChunkLocation()
	{
		return chunkStorageLocation;
	}
	
	public static String getClientTempLocation()
	{
		return TEMP_LOCATION;
	}
	
	public static InetSocketAddress getLocalChunkNodeAddress()
	{
		try {
			return new InetSocketAddress(InetAddress.getByName(slaveHostName), slavePort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static InetSocketAddress getMetaNodeAddress()
	{
		try {
			return new InetSocketAddress(InetAddress.getByName(masterHostName), masterPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
