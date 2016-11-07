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

public interface PropertyResolver {
	
	public String getProperty(String key) throws PropertyNotFoundException;
	
	public int getIntProperty(String key) throws PropertyNotFoundException, NumberFormatException;
    
	public String[] getProperties(String key) throws PropertyNotFoundException;
}
