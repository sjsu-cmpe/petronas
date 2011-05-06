package com.tdfs.fs.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class ConfigParser {
	
	private Config config = null;
	private	List<SchedulerConfig> schedulers =null;
	
	public Config parseConfigurationFile()
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		Document configDocument = null;
		
		this.config = new Config();
		this.schedulers = new ArrayList<SchedulerConfig>();
		
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			configDocument = documentBuilder.parse("config.xml");
			
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element element = configDocument.getDocumentElement();
		NodeList nodelist = element.getElementsByTagName("Master");
		
		if(nodelist != null && nodelist.getLength()>0)
		{
			Element master = (Element) nodelist.item(0);
			NodeList nl = master.getElementsByTagName("HostIP");
			Element el = (Element) nl.item(0);
			config.setMasterHostName(el.getFirstChild().getNodeValue());
			nl = master.getElementsByTagName("Port");
			el = (Element) nl.item(0);
			config.setMasterPort(Integer.parseInt(el.getFirstChild().getNodeValue()));
			nl = master.getElementsByTagName("StorageLocation");
			el = (Element) nl.item(0);
			config.setMasterStorageLocation(el.getFirstChild().getNodeValue());
		}
		
		// Load Slave config
		nodelist = element.getElementsByTagName("Slave");
		
		if(nodelist != null && nodelist.getLength()>0)
		{
			Element slave = (Element) nodelist.item(0);
			NodeList nl = slave.getElementsByTagName("HostIP");
			Element el = (Element) nl.item(0);
			config.setSlaveHostName(el.getFirstChild().getNodeValue());
			nl = slave.getElementsByTagName("Port");
			el = (Element) nl.item(0);
			config.setSlavePort(Integer.parseInt(el.getFirstChild().getNodeValue()));
			nl = slave.getElementsByTagName("StorageLocation");
			el = (Element) nl.item(0);
			config.setSlaveStorageLocation(el.getFirstChild().getNodeValue());
		}
		
		// Load schedulers config
		
		nodelist = element.getElementsByTagName("Scheduler");
		loadSchedulerConfig(nodelist);
		
		config.setSchedulersEnabled(schedulers);
		
		nodelist = element.getElementsByTagName("ChunkSize");
		
		if(nodelist != null && nodelist.getLength()>0)
		{
			Element chunkSize = (Element) nodelist.item(0);
			
			config.setChunkSize(Long.parseLong(chunkSize.getFirstChild().getNodeValue()));
		}
		
		
		return this.config;
	}
	
	private void loadSchedulerConfig(NodeList nodelist)
	{
		if(nodelist != null && nodelist.getLength()>0)
		{
			SchedulerConfig scheduleConfig;
			for(int i=0;i<nodelist.getLength();i++)
			{
				scheduleConfig = new SchedulerConfig();
				Element scheduler = (Element) nodelist.item(i);
				
				NodeList nl = scheduler.getElementsByTagName("Name");
				Element el = (Element) nl.item(0);
				scheduleConfig.setSchedulerName(el.getFirstChild().getNodeValue());
				nl = scheduler.getElementsByTagName("InitialDelay");
				el = (Element) nl.item(0);
				scheduleConfig.setInitialDelay(Long.parseLong(el.getFirstChild().getNodeValue()));
				nl = scheduler.getElementsByTagName("RepeatDelay");
				el = (Element) nl.item(0);
				scheduleConfig.setRepeatPeriod(Long.parseLong(el.getFirstChild().getNodeValue()));
				
				this.schedulers.add(scheduleConfig);
			}
			
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new ConfigParser().parseConfigurationFile();

	}

}
