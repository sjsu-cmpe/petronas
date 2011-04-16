package com.tdfs.fs.metanode.element;

import java.io.Serializable;
import java.util.List;
import java.util.zip.CRC32;

public class File implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6472560210717547781L;
	private String fileName;
	private int VERSION = 1;
	private List<String> blockList;
	private long checksum;
	private long fileSize;
	
	public File(String fileName)
	{
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public int getVERSION() {
		return VERSION;
	}
	public void setVERSION(int vERSION) {
		VERSION = vERSION;
	}
	public List<String> getBlockList() {
		return blockList;
	}
	public void setBlockList(List<String> blockNamesList) {
		this.blockList = blockNamesList;
	}
	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}
	public long getChecksum() {
		return checksum;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getFileSize() {
		return fileSize;
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
