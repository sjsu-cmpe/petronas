package com.tdfs.interfaces.filesystem;




public interface FileSystem {
	
	public boolean mkdir(String directoryPath);
	
	public boolean read(String filePath);
	
	public boolean move(String localFilePath, String dfsFilePath);
	
	public boolean readdir(String directoryPath);

	
	

}
