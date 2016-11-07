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
 * Stores and provides access to all the services in the application
 */
public class ServiceCollection
{
    /*********************************************************************************************
     * VARIABLE DECLARATIONS                                                                     *
     *********************************************************************************************/ 

    // Singleton instance
    private static ServiceCollection _instance;

    private MDSSearchService mdsSearch;
    private TransformerService transformer;

    /*********************************************************************************************
     * CONSTRUCTORS AND PUBLIC INITIALISATION                                                    *
     *********************************************************************************************/

    public static void initialize( ServletContext servletContext ) throws Exception
    {
        _instance = new ServiceCollection( servletContext );
    }

    public static void destroy() throws Exception
    {
        if( _instance != null )
        {
            _instance.destroyImpl();
            _instance = null;
        }
    }

    public static ServiceCollection getInstance()
    {
        return _instance;
    }

    private ServiceCollection( ServletContext servletContext ) throws Exception
    {
        // Initialize our services
        mdsSearch = new MDSSearchServiceSolrImpl(servletContext);
        transformer = new TransformerService(servletContext);
    }

    private void destroyImpl() throws Exception
    {
        mdsSearch.destroy();
        transformer.destroy();
    }

    /*********************************************************************************************
     * PUBLIC METHODS                                                                            *
     *********************************************************************************************/
    
    public MDSSearchService getMDSSearchService(){
        return mdsSearch;
    }
    
    public TransformerService getTransformerService(){
        return transformer;
    }
    
    /*********************************************************************************************
     * PRIVATE METHODS                                                                           *
     *********************************************************************************************/

}
