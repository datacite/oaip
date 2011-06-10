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

import org.apache.log4j.Logger;

/**
 * Controller servlet specific to the DataCite MDS repository
 * @author PaluchM
 */
public class DataCiteControllerServlet extends ApplicationControllerServlet{

    private static final long serialVersionUID = 1L;    
    private static final Logger logger = Logger.getLogger(DataCiteControllerServlet.class);         
    
    @Override
    protected String getRepositoryID() {
        return Constants.RepositoryID.DATACITEMDS;
    }

    @Override
    protected String getApplicationName() {
        return "DataCite OAI-PMH Provider";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
