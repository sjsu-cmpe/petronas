package com.tdfs.fs.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DiskPersistence<T> {
	private FileOutputStream fileOut;
	private FileInputStream fileIn;
	private ObjectOutputStream objectOut;
	private ObjectInputStream objectIn;
		
	public void writeObjectToDisk(T obj,String path)
	{
		try {
			fileOut = new FileOutputStream(path);
			objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(obj);
			objectOut.flush();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			this.closeOutStreams();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public T readObjectFromDisk(String path)
	{
		T object = null;
		try {
			fileIn = new FileInputStream(path);
			objectIn = new ObjectInputStream(fileIn);
			object = (T) objectIn.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			this.closeInStreams();
			
		}
		return object;
	}
	
	private boolean closeInStreams()
	{
		try {
			if(objectIn != null)
				objectIn.close();
			if(fileIn != null)
				fileIn.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	private boolean closeOutStreams()
	{
		try {
			if(objectOut != null)
				objectOut.close();
			if(fileOut != null)
				fileOut.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

}
