package com.tdfs.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.CRC32;

import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.FileReadDataPacket;
import com.tdfs.ipc.element.FileWriteDataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.io.AbstractClient;

public class DistributedFileSystem extends AbstractClient{
	
	DistributedFileSystem(InetAddress host, int port) {
		super(host, port);
		
	}

	File file = null;
	private FileInputStream localInputStream = null;
	private FileOutputStream localOutputStream = null;
	private FileWriteDataPacket fileWriteDataPacket;
	private FileReadDataPacket fileReadDataPacket;
	private DataPacket<?> dataPacket;
	
	
	public boolean isMetaNodeReady()
	{
		return true;
	}
	
	public boolean moveFile(String localFilePath)
	{
		CRC32 checkSum = new CRC32();
		boolean isLastChunk = false;
		byte[] chunkBytes = null;
		int readByteCount = 0;
		int chunkCount = 0;
		try {
			file = new File(localFilePath);
			localInputStream = new FileInputStream(file);
			int fileSize = (int) file.length();
			int readLength = ResourceLoader.getBlockSize();
			
			while (fileSize > 0) {
	            if (fileSize <= readLength) {
	                readLength = fileSize;
	                isLastChunk = true;
	            }
	            chunkBytes = new byte[(int) readLength];
	            chunkCount++;
	            readByteCount = localInputStream.read(chunkBytes, 0, (int) readLength);
	                       
	            fileSize -= readByteCount;
	            
	            checkSum.update(chunkBytes);
	            
				// TODO: Send it to the MetaNode
	            fileWriteDataPacket = new FileWriteDataPacket(PacketType.FILE_WRITE, chunkBytes, System.currentTimeMillis(),
	            		 new InetSocketAddress(InetAddress.getByName("localhost"), 9191),file.getName(),checkSum.getValue(),chunkCount, isLastChunk);
	            initiateConnection();
	            sendMessage(fileWriteDataPacket);
	            
	            DataPacket<?> packet = this.responseHandler();
	            System.out.println(" Response ");
	            System.out.println(packet.getData());
	                       
	            //finishConnection();
	        }
			//System.out.println(this.responseHandler().getData());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	public boolean read(String fileName)
	{
		LinkedList<String> chunkList = null;
		Map<String,InetSocketAddress> chunkMap = null;
		DistributedFileSystem tempClient = null;
		File tempFile = new File(ResourceLoader.getClientTempLocation()+fileName);
		
		try {
			tempFile.createNewFile();
			localOutputStream = new FileOutputStream(tempFile,true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			dataPacket = new DataPacket<String>(PacketType.FILE_READ, 
					fileName, System.currentTimeMillis(), 
					new InetSocketAddress(InetAddress.getByName("localhost"), 9191));
			initiateConnection();
			sendMessage(dataPacket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileReadDataPacket =  (FileReadDataPacket) responseHandler();
		chunkMap = fileReadDataPacket.getData();
		if(fileReadDataPacket != null)
		{
			//TODO: Check the checkSum before writing
			chunkList = fileReadDataPacket.getChunksList();
			for(String chunkName : chunkList)
			{
				InetSocketAddress chunkNodeAddress = chunkMap.get(chunkName);
				tempClient = new DistributedFileSystem(chunkNodeAddress.getAddress(), chunkNodeAddress.getPort());
				tempClient.initiateConnection();
				tempClient.sendMessage(new DataPacket<String>(PacketType.CHUNK_READ, chunkName, System.currentTimeMillis(), null));
				DataPacket<Chunk> chunkData = (DataPacket<Chunk>) tempClient.responseHandler();
				//TODO: Move the exception handling
				try {
					localOutputStream.write(chunkData.getData().getBlockBytes());
					localOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			//TODO: Consolidate Try Catch
			try {
				localOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	

	/* TODO: Not required for FS Client Refactoring required */
	@Override
	public DataPacket<?> call() throws Exception {
		
		dataPacket = this.responseHandler();
		return dataPacket;
		
		
	}
	
	public static void main(String... args)
	{
		try {
			DistributedFileSystem localClient = new DistributedFileSystem(InetAddress.getByName("localhost"), 9090);
			//localClient.moveFile("/home/gisripa/dfs-testdata/movie.wmv");
			localClient.read("movie.wmv");
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
