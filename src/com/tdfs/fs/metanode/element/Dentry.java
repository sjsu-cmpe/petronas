package com.tdfs.fs.metanode.element;

import java.io.Serializable;

public class Dentry implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Dentry parent;
	public Dentry(String name,Dentry parent)
	{
		this.name = name;
		this.parent = parent;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Dentry getParent()
	{
		return this.parent;
	}

}
