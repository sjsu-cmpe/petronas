package com.tdfs.fs.util;

import java.util.UUID;

public class IdGenerator {
	
	// TODO: Improve it, works as of now
	public static String getId(byte[] data)
	{
		StringBuilder idStringBuilder = new StringBuilder("chunk");
		idStringBuilder.append(UUID.nameUUIDFromBytes(data).getLeastSignificantBits());
		return idStringBuilder.toString();
		
		
	}
	

}
