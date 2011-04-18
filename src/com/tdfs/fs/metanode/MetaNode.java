package com.tdfs.fs.metanode;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.tdfs.fs.io.DiskPersistence;
import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.fs.metanode.element.INode;
import com.tdfs.fs.scheduler.AbstractScheduler;
import com.tdfs.fs.scheduler.MetadataSnapshot;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.DataEvent;
import com.tdfs.ipc.io.AbstractServer;

//TODO: Change the structure of the inheritance. Try Event-Driven design
public class MetaNode extends AbstractServer{
	
	public FSMetadata metadata;
	private DataEvent dataEvent = null;
	private SocketEventHandler socketEventHandler = null;
	private FileWriteHandler fileWriteHandler = null;
	private FileReadHandler fileReadHandler = null;
	private AbstractScheduler scheduler = null;
	
	private static Logger logger = Logger.getLogger(MetaNode.class);
	
	public MetaNode(InetAddress host,int port)
	{
		super.startServer(host, port);
		this.initMetaNode();
		
				
	}
	
	private void initMetaNode()
	{
		logger.info("Entering Initialization Mode...");
		metadata = FSMetadata.getInstance();
		registerEventHandlers();
		scheduler = new MetadataSnapshot(3000, 3000);
				
	}
	
	private void registerEventHandlers()
	{
		logger.debug("Registering Event Handlers for MetaNode...");
		this.dataEvent = new DataEvent();
		this.socketEventHandler = new SocketEventHandler();
		this.fileWriteHandler = new FileWriteHandler();
		this.fileReadHandler = new FileReadHandler();
		dataEvent.addObserver(this.socketEventHandler);
		dataEvent.addObserver(this.fileWriteHandler);
		dataEvent.addObserver(this.fileReadHandler);
	}
	
		
	private void saveMetadataToDisk(String filePath)
	{
		// TODO: Save on Exit.
	}

	
	@Override
	public void registerEvent(Socket socket,DataPacket<?> dataPacket)
	{
		this.dataEvent.setDataPacket(socket,dataPacket);
		
	}
	
		
	public static void main(String[] args)
	{
		try {
			AbstractServer server = new MetaNode(InetAddress.getByName("localhost"), 9090);
			new Thread(server).start();
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
