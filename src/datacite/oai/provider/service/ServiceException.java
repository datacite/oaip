package datacite.oai.provider.service;

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

public class ServiceException extends Exception {

	private static final long serialVersionUID = 1;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public ServiceException(Throwable throwable) {
		super(throwable);
	}
}
