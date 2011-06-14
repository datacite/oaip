package datacite.oai.provider;

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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import datacite.oai.provider.catalog.datacite.MDSSetCache;
import datacite.oai.provider.service.ServiceCollection;

/**
 * Initializes and destroys the context
 */
public final class ContextListener implements ServletContextListener
{
    /*********************************************************************************************
     * VARIABLE DECLARATIONS                                                                     *
     *********************************************************************************************/

    private static final Logger logger = Logger.getLogger(ContextListener.class);

    /*********************************************************************************************
     * CONSTRUCTORS AND PUBLIC INITIALISATION                                                    *
     *********************************************************************************************/

    /*********************************************************************************************
     * PUBLIC METHODS                                                                            *
     *********************************************************************************************/

    public synchronized void contextInitialized( ServletContextEvent event )
    {
        logger.warn( "STARTING UP" );

        try
        {
            ServletContext servletContext = event.getServletContext();

            ApplicationContext.initialize(servletContext);
            ServiceCollection.initialize(servletContext);
            MDSSetCache.initialize();
        }
        catch( Exception e )
        {
            logger.fatal("Unable to initalize context",e);
            throw new IllegalStateException("Unable to initalize context",e);
        }
    }

    public synchronized void contextDestroyed(ServletContextEvent event)
    {
        logger.warn("SHUTTING DOWN");

        try
        {
            // Destroy services
            ServiceCollection.destroy();
        }
        catch(Exception e)
        {
            logger.fatal( "Unable to destroy context", e );
        }
    }

    /*********************************************************************************************
     * PRIVATE METHODS                                                                           *
     *********************************************************************************************/

}
