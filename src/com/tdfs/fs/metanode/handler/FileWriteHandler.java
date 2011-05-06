package com.tdfs.fs.metanode.handler;



import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.metanode.MetaClient;
import com.tdfs.fs.metanode.element.Dentry;
import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.fs.metanode.element.INode;
import com.tdfs.fs.util.IdGenerator;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.FileWriteDataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;
import com.tdfs.ipc.io.AbstractClient;


/**
 * @author       gisripa
 */
public class FileWriteHandler extends AbstractEventListener{
	/**
	 */
	FileWriteDataPacket fileDataPacket = null;
	/**
	 */
	INode file = null;
	List<String> chunkNames = null;
	long fileSize = 0;
	List<Chunk> chunksToTransfer = null;
	/**
	 */
	FSMetadata metadata = null;
	InetSocketAddress chunkNodeAddress = null;
	
	private static Logger logger = Logger.getLogger(FileWriteHandler.class);
	
	public FileWriteHandler() {
		
		metadata = FSMetadata.getInstance();
		
	}

	

	@Override
	public void update(Observable arg0, Object arg1) 
	{
		DataEvent event = (DataEvent) arg0;		
		if(arg1 instanceof DataPacket<?>)
		{
			if(((DataPacket<?>) arg1).getPacketType() == PacketType.FILE_WRITE)
			{
				if(this.file == null)
				{
					this.chunkNodeAddress = metadata.getAvailableChunkNode();
				}
				if(this.chunkNodeAddress != null)
				{
					this.fileDataPacket = (FileWriteDataPacket) arg1;
					createDirectoryStructure(this.fileDataPacket.getFileName(), this.fileDataPacket.isDirectory());
					if(!this.fileDataPacket.isDirectory())
					{
						createFileObject();
					}
					
					sendAcknowledgement(event.getEventSocket());
				}
				else{
					sendErrorNotification(event.getEventSocket());
				}
				
			}
			
			
			
		}
	}
	
	private void createFileObject()
	{
		//TODO: Verify checkSum
		/**
		 *  When first packet received create all instances of objects and start building the File.
		 *  In addition, send the chunks to respective chunk nodes.
		 */
		byte[] chunk = fileDataPacket.getData();
		String[] tokens = fileDataPacket.getFileName().split("/");
		int length = tokens.length;
		String fileName = tokens[length-1];
		Dentry parentDentry = null;
		
		if(length >= 2)
		{
			
			if(!tokens[length-2].equals(""))
			{
				parentDentry = metadata.getINode(tokens[length-2]).getDirectoryEntry();
				logger.debug("File Directory added as -->"+parentDentry.getName());
				
			}
		}
		
		if(file == null)
		{
			chunkNames = new LinkedList<String>();
			chunksToTransfer = Collections.synchronizedList(new ArrayList<Chunk>());
			
			file = new INode(fileName);
			file.setChecksum(fileDataPacket.getCheckSum());
			file.setFileSize(chunk.length);
			file.setVERSION(1);
			file.setDirectory(false);
			file.setDirectoryEntry(parentDentry);
		
		}
		else
		{
			file.setChecksum(file.getChecksum()+fileDataPacket.getCheckSum());
			file.setFileSize(file.getFileSize()+chunk.length);
		}
		
		Chunk chunkData = new Chunk();
		chunkData.setBlockBytes(chunk);
		chunkData.setChunkFileName(IdGenerator.getId(chunk));
		chunkNames.add(chunkData.getChunkFileName());
		chunksToTransfer.add(chunkData);
		
		if(fileDataPacket.isLastPacket())
		{
			file.setChunkList(chunkNames);
			metadata.updateINodeMap(file.getFileName(), file);
			logger.info("File creation completed-->"+file.toString());
			//TODO: Checksum check before sending else throw exception
			sendChunks();
			file = null;
			chunkNames = null;
			chunksToTransfer = null;
			chunkNodeAddress = null;
			
		}
		
		
		
	}
	
	private void createDirectoryStructure(String path,boolean isDirectory)
	{
		String[] tokens = path.split("/");
		Dentry parentDirectory = null;
		int endLimit = 0;
		if(isDirectory)
		{
			endLimit = tokens.length;
		}
		else{
			endLimit = tokens.length - 1;
		}
		for(int index=0;index<endLimit;index++)
		{
			if(index>=1)
			{
				if(!tokens[index-1].equals(""))
				{
					parentDirectory = metadata.getINode(tokens[index-1]).getDirectoryEntry();
				}
				logger.debug("Creating Directory --> "+tokens[index-1]+"/"+tokens[index]);
				createDirectoryInodeEntry(tokens[index], parentDirectory);
			}
			else
			{
				if(!tokens[index].equals(""))
				{
					logger.debug("Creating Directory --> /Home/"+tokens[index]);
					createDirectoryInodeEntry(tokens[index], metadata.getINode("Home").getDirectoryEntry());
				}
				
			}
			
		}
	}
	
	private void createDirectoryInodeEntry(String directoryName,Dentry parentDirectory)
	{
		INode iNode = new INode(directoryName);
		Dentry dentry = new Dentry(directoryName,parentDirectory);
		
		iNode.setDirectory(true);
		iNode.setDirectoryEntry(dentry);
		metadata.updateINodeMap(directoryName, iNode);
	}
		
	
	private void sendAcknowledgement(Socket socket)
	{
		sendResponse(socket, 
				new DataPacket<String>(PacketType.ACKNOWLEDGEMENT, "Chunk Received by MetaNode", System.currentTimeMillis(), null));
	}
	
	private void sendErrorNotification(Socket socket)
	{
		sendResponse(socket, 
				new DataPacket<String>(PacketType.ERROR, "No Chunk Node active, please contact sysadmin", System.currentTimeMillis(), null));
	}

	//TODO: Initiate send chunks to chunk nodes
	public void sendChunks()
	{
		logger.debug("Chunks transferred for persistence");
		AbstractClient client = null;
		Iterator<Chunk> iterator = chunksToTransfer.iterator();
		ExecutorService executorPool = Executors.newFixedThreadPool(6);
		Set<Future<DataPacket<?>>> responseSet = new HashSet<Future<DataPacket<?>>>();
		
		while(iterator.hasNext())
		{
			Chunk chunk = iterator.next();
			try {
				client = new MetaClient(chunkNodeAddress.getAddress(),chunkNodeAddress.getPort(),
						new DataPacket<Chunk>(PacketType.CHUNK_WRITE, chunk, System.currentTimeMillis(),null));
				client.initiateConnection();
			} catch (Exception e) {
				logger.error("Weird Exception", e);
			}
			Future<DataPacket<?>> response = executorPool.submit(client);
			responseSet.add(response);
			
			metadata.updateChunkLocationMap(chunk.getChunkFileName(), chunkNodeAddress);
		}
		
		for(Future<DataPacket<?>> response: responseSet)
		{
			try {
				DataPacket<?> packet = response.get();
				logger.info("Response from ChunkNode -->"+packet.getData());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	

}
