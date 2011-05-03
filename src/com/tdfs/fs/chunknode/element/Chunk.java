package com.tdfs.fs.chunknode.element;

import java.io.Serializable;

/**
 * @author       gisripa
 */
public class Chunk implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6866873760616615772L;
	private byte[] chunkBytes;
	/**
	 */
	private String chunkFileName;
	
	
	public byte[] getBlockBytes() {
		return chunkBytes;
	}
	public void setBlockBytes(byte[] blockBytes) {
		this.chunkBytes = blockBytes;
	}
	/**
	 * @param  chunkFileName
	 */
	public void setChunkFileName(String chunkFileName) {
		this.chunkFileName = chunkFileName;
	}
	/**
	 * @return
	 */
	public String getChunkFileName() {
		return chunkFileName;
	}
	

}
