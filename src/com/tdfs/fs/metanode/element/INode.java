package com.tdfs.fs.metanode.element;

import java.io.Serializable;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @author       gisripa
 */
public class INode implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6472560210717547781L;
	/**
	 */
	private String fileName;
	/**
	 */
	private int VERSION = 1;
	/**
	 */
	private List<String> blockList;
	/**
	 */
	private long checksum;
	/**
	 */
	private long fileSize;
	/**
	 */
	private boolean isDirectory;
	/**
	 */
	private Dentry directoryEntry;
	
	public INode(String fileName)
	{
		this.fileName = fileName;
	}
	
	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * @return
	 */
	public int getVERSION() {
		return VERSION;
	}
	/**
	 * @param  VERSION
	 */
	public void setVERSION(int VERSION) {
		this.VERSION = VERSION;
	}
	/**
	 * @return
	 */
	public List<String> getChunkList() {
		return blockList;
	}
	/**
	 * @param  chunkNamesList
	 */
	public void setChunkList(List<String> chunkNamesList) {
		this.blockList = chunkNamesList;
	}
	/**
	 * @param  checksum
	 */
	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}
	/**
	 * @return
	 */
	public long getChecksum() {
		return checksum;
	}
	/**
	 * @param  fileSize
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @return
	 */
	public long getFileSize() {
		return fileSize;
	}
	
	/**
	 * @param  directory
	 */
	public void setDirectory(boolean directory) {
		this.isDirectory = directory;
	}

	/**
	 * @return
	 */
	public boolean isDirectory() {
		return isDirectory;
	}

	/**
	 * @param  directoryEntry
	 */
	public void setDirectoryEntry(Dentry directoryEntry) {
		this.directoryEntry = directoryEntry;
	}

	/**
	 * @return
	 */
	public Dentry getDirectoryEntry() {
		return directoryEntry;
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("File->").append(fileName).append(" ");
		stringBuilder.append("File Info->").append("Size:").append(fileSize).append(" Bytes ");
		stringBuilder.append("List of Chunks: ").append(blockList.toString());
		return stringBuilder.toString();
	}

}
