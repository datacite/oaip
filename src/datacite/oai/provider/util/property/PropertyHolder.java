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
import java.util.Set;

public interface PropertyHolder {
	
    public void setProperty(String key, String value);
    public void setProperties(Properties properties);
    public Set<Object> getKeys();

}
