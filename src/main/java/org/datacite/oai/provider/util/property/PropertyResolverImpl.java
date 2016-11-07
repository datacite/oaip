package datacite.oai.provider.util.property;

/*******************************************************************************
* Copyright (c) 2011 DataCite
*
* All rights reserved. This program and the accompanying 
* materials are made available under the terms of the 
* Apache License, Version 2.0 which accompanies 
* this distribution, and is available at 
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/

import java.util.Properties;

public class PropertyResolverImpl implements PropertyResolver {
	
	private Properties properties;
	
	public PropertyResolverImpl(Properties properties){
		this.properties = properties;
	}
	
	public String getProperty(String key) throws PropertyNotFoundException {
		Object value = this.properties.getProperty(key);
		if(value == null){
			throw new PropertyNotFoundException(" Requested property key "+ key +" is not found in PropertyHolder" );
		}
		
		return (String) value;
	}
	
	

	public int getIntProperty(String key) throws PropertyNotFoundException, NumberFormatException{
		String value = (String) this.properties.getProperty(key);
		if(value == null){
			throw new PropertyNotFoundException(" Requested property key "+ key +" is not found in PropertyHolder" );
		}
		
		
		return Integer.parseInt(value);
	}
	
	public String[] getProperties (String key) throws PropertyNotFoundException{
		String value = (String) this.properties.getProperty(key);
		if(value == null){
			throw new PropertyNotFoundException(" Requested property key "+ key +" is not found in PropertyHolder" );
		}
		
		String[] values = value.split("[,]");
		return values;
	}
	
	public static void main(String[] args)throws Exception{
		Properties prop = new Properties();
		prop.put("test","cis,pricing");
		PropertyResolver presolver = new PropertyResolverImpl(prop);
		String[] values =presolver.getProperties("test");
		for (String string : values) {
			System.out.println(string);
		}
		
		
	}

}
