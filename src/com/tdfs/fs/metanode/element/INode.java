package com.tdfs.fs.metanode.element;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class INode implements Serializable{
	
	private UUID id;
	private long sizeOfFile;
	private long timeStamp;
	private List<String> blockList;
 
}
