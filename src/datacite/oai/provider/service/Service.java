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

import javax.servlet.ServletContext;

/**
 *  Service interface is a marker interface
 */
public abstract class Service
{
	/*********************************************************************************************
	 * VARIABLE DECLARATIONS                                                                     *
	 *********************************************************************************************/

	/*********************************************************************************************
	 * CONSTRUCTORS AND PUBLIC INITIALISATION                                                    *
	 *********************************************************************************************/

	public Service(ServletContext context)
	{
	}

	/*********************************************************************************************
	 * PUBLIC METHODS                                                                            *
	 *********************************************************************************************/

	public abstract void destroy();

	/*********************************************************************************************
	 * PRIVATE METHODS                                                                           *
	 *********************************************************************************************/    

}
