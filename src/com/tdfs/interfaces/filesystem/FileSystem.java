package com.tdfs.interfaces.filesystem;

import java.util.List;

import com.tdfs.fs.chunknode.element.Chunk;
import com.tdfs.fs.metanode.element.File;



public interface FileSystem {
	
	public boolean mkdir(String path);
	
	public boolean read(String filePath);
	
	public boolean write(File fileData,List<Chunk> chunks);
	
	
	

}
