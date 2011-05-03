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
import com.tdfs.fs.metanode.element.Dentry;
import com.tdfs.fs.util.ResourceLoader;
import com.tdfs.interfaces.filesystem.FileSystem;
import com.tdfs.ipc.element.DataPacket;
import com.tdfs.ipc.element.FileReadDataPacket;
import com.tdfs.ipc.element.FileWriteDataPacket;
import com.tdfs.ipc.element.PacketType;
import com.tdfs.ipc.io.AbstractClient;

/**
 * @author       gisripa
 */
public class DistributedFileSystem extends AbstractClient implements FileSystem{

	DistributedFileSystem(InetAddress host, int port) {
		super(host, port);

	}

	File file = null;
	private FileInputStream localInputStream = null;
	private FileOutputStream localOutputStream = null;
	/**
	 */
	private FileWriteDataPacket fileWriteDataPacket;
	/**
	 */
	private FileReadDataPacket fileReadDataPacket;
	/**
	 */
	private DataPacket<?> dataPacket;


	public boolean isMetaNodeReady()
	{
		return true;
	}

	@Override
	public boolean move(String localFilePath,String dfsFilePath)
	{
		CRC32 checkSum = new CRC32();
		boolean isLastChunk = false;
		byte[] chunkBytes = null;
		int readByteCount = 0;
		int chunkCount = 0;
		String[] pathTokens = dfsFilePath.split("/");
		StringBuilder filePath = new StringBuilder(dfsFilePath);
		
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
				if(pathTokens[pathTokens.length-1].equals(""))
				{
					filePath.append(file.getName());
				}
				// TODO: Send it to the MetaNode
				fileWriteDataPacket = new FileWriteDataPacket(PacketType.FILE_WRITE, chunkBytes, System.currentTimeMillis(),
						new InetSocketAddress(InetAddress.getByName("localhost"), 9191),filePath.toString(),checkSum.getValue(),chunkCount, isLastChunk, false);
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

	@Override
	public boolean read(String fileName)
	{
		LinkedList<String> chunkList = null;
		Map<String,InetSocketAddress> chunkMap = null;
		DistributedFileSystem tempClient = null;
		File tempFile = new File(ResourceLoader.getClientTempLocation()+fileName);

		try 
		{
			tempFile.createNewFile();
			localOutputStream = new FileOutputStream(tempFile,true);

			dataPacket = new DataPacket<String>(PacketType.FILE_READ, 
					fileName, System.currentTimeMillis(), 
					ResourceLoader.getLocalChunkNodeAddress());
			initiateConnection();
			sendMessage(dataPacket);

			dataPacket =  (DataPacket<?>) responseHandler();
			if(dataPacket.getPacketType() == PacketType.ERROR)
			{
				System.out.println(dataPacket.getData());
			}
			else{
				fileReadDataPacket = (FileReadDataPacket) dataPacket;
			}
			
			
			if(fileReadDataPacket != null)
			{
				//TODO: Check the checkSum before writing
				chunkMap = fileReadDataPacket.getData();
				System.out.println(getDirStructure(fileReadDataPacket.getDirectoryEntry()));
				chunkList = fileReadDataPacket.getChunksList();
				for(String chunkName : chunkList)
				{
					InetSocketAddress chunkNodeAddress = chunkMap.get(chunkName);
					tempClient = new DistributedFileSystem(chunkNodeAddress.getAddress(), chunkNodeAddress.getPort());
					tempClient.initiateConnection();
					tempClient.sendMessage(new DataPacket<String>(PacketType.CHUNK_READ, chunkName, System.currentTimeMillis(), ResourceLoader.getLocalChunkNodeAddress()));
					DataPacket<Chunk> chunkData = (DataPacket<Chunk>) tempClient.responseHandler();
					//TODO: Move the exception handling
					try {
						localOutputStream.write(chunkData.getData().getBlockBytes());
						localOutputStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Thread.sleep(5000);

				}

				//TODO: Consolidate Try Catch
				try {
					localOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch(Exception e)
		{

		}


		return true;
	}

	private String getDirStructure(Dentry dentry)
	{
		StringBuilder sb = new StringBuilder();
		if(dentry.getParent() == null)
		{
			return sb.append("/").append(dentry.getName()).toString();
		}
		
		return sb.append(getDirStructure(dentry.getParent())).append("/").append(dentry.getName()).toString();
		
		
	}



	@Override
	public boolean mkdir(String directoryPath) {
		fileWriteDataPacket = 
			new FileWriteDataPacket(PacketType.FILE_WRITE, 
					new String(directoryPath).getBytes(), 
					System.currentTimeMillis(), ResourceLoader.getLocalChunkNodeAddress(), 
					directoryPath, 0, 0, true, true);
		initiateConnection();
		sendMessage(fileWriteDataPacket);
		
		DataPacket<?> dataPacket = this.responseHandler();
		System.out.println("Response from MetaNode"+dataPacket.getData());
		
		return true;
	}



	@Override
	public boolean readdir(String directoryPath) {
		// TODO Auto-generated method stub
		return false;
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
			final DistributedFileSystem localClient = new DistributedFileSystem(InetAddress.getByName("localhost"), 9090);
			//localClient.move("/home/gisripa/dfs-testdata/music2.mp3","gisripa/music/music2.mp3");
			/*new Thread(new Runnable() {
				
				@Override
				public void run() {
					localClient.move("/home/gisripa/dfs-testdata/music1.mp3","gisripa/music/music1.mp3");
					
				}
			}
					).start();*/
			localClient.read("music2.mp3");
			//localClient.read("movie.wmv");
			//localClient.mkdir("/songs/ladygaga/2001");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
