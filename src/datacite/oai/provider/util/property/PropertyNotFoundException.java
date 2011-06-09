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

public class PropertyNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public PropertyNotFoundException() {
		super();		
	}

	public PropertyNotFoundException(String message, Throwable cause) {
		super(message, cause);		
	}

	public PropertyNotFoundException(String message) {
		super(message);		
	}

	public PropertyNotFoundException(Throwable cause) {
		super(cause);		
	}

}
