package com.tdfs.fs.metanode;


import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.metanode.element.FSMetadata;
import com.tdfs.fs.metanode.element.File;
import com.tdfs.fs.util.IdGenerator;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.FileWriteDataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.event.AbstractEventListener;
import com.tdfs.ipc.event.DataEvent;
import com.tdfs.ipc.io.AbstractClient;


public class FileWriteHandler extends AbstractEventListener{
	FileWriteDataPacket fileDataPacket = null;
	File file = null;
	List<String> chunkNames = null;
	long fileSize = 0;
	List<Chunk> chunksToTransfer = null;
	FSMetadata metadata = null;
	
	public FileWriteHandler() {
		
		metadata = FSMetadata.getInstance();
		chunksToTransfer = Collections.synchronizedList(new ArrayList<Chunk>());
	}

	

	@Override
	public void update(Observable arg0, Object arg1) 
	{
		DataEvent event = (DataEvent) arg0;		
		if(arg1 instanceof DataPacket<?>)
		{
			if(((DataPacket<?>) arg1).getPacketType() == PacketType.FILE_WRITE)
			{
				this.fileDataPacket = (FileWriteDataPacket) arg1;
				createFileObject();
				sendAcknowledgement(event.getEventSocket());
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
		
		if(file == null)
		{
			chunkNames = new LinkedList<String>();
			
			file = new File(fileDataPacket.getFileName());
			file.setChecksum(fileDataPacket.getCheckSum());
			file.setFileSize(chunk.length);
			file.setVERSION(1);
		
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
			file.setBlockList(chunkNames);
			metadata.updateFileMetadata(file.getFileName(), file);
			System.out.println("File creation completed-->"+file.toString());
			//TODO: Checksum check before sending else throw exception
			sendChunks();
			file = null;
			chunkNames = null;
			
		}
		
		
		
	}
	
	private void sendAcknowledgement(Socket socket)
	{
		sendResponse(socket, 
				new DataPacket<String>(PacketType.ACKNOWLEDGEMENT, "Chunk Received", System.currentTimeMillis(), null));
	}

	//TODO: Initiate send chunks to chunk nodes
	public void sendChunks()
	{
		System.out.println("Sending chunks for persistence");
		AbstractClient client = null;
		Iterator<Chunk> iterator = chunksToTransfer.iterator();
		ExecutorService executorPool = Executors.newFixedThreadPool(6);
		Set<Future<DataPacket<?>>> responseSet = new HashSet<Future<DataPacket<?>>>();
		while(iterator.hasNext())
		{
			Chunk chunk = iterator.next();
			try {
				client = new MetaClient(InetAddress.getByName(metadata.getAvailableChunkNode().getHostName()),metadata.getAvailableChunkNode().getPort(),
						new DataPacket<Chunk>(PacketType.CHUNK_WRITE, chunk, System.currentTimeMillis(),null));
				client.initiateConnection();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Future<DataPacket<?>> response = executorPool.submit(client);
			responseSet.add(response);
			
			metadata.updateChunkLocationMap(chunk.getChunkFileName(), metadata.getAvailableChunkNode());
		}
		
		for(Future<DataPacket<?>> response: responseSet)
		{
			try {
				DataPacket<?> packet = response.get();
				System.out.println("Response from chunknode"+packet.getData());
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
