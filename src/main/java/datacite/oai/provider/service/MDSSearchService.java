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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import datacite.oai.provider.ApplicationContext;
import datacite.oai.provider.Constants;
import datacite.oai.provider.catalog.datacite.SetRecordBean;
import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.db.DatasetRowMapper;
import datacite.oai.provider.db.PooledDataSource;
import datacite.oai.provider.db.SetRowMapper;
import datacite.oai.provider.util.Pair;
import datacite.oai.provider.util.ThreadSafeSimpleDateFormat;
import datacite.oai.provider.util.property.PropertyNotFoundException;

/**
 * Encapsulates all methods required for returning Records and Sets from the DataCite MDS.
 * @author PaluchM
 *
 */
public abstract class MDSSearchService extends Service {
    
    /**
     * Public constructor.
     * @param servletContext
     * @throws ServiceException
     */
    public MDSSearchService(ServletContext servletContext) throws ServiceException {
        super(servletContext);
    }

    /**
     * Returns a dataset for the given id.
     * @param id
     * @return The dataset for the associated id or null if not found.
     * @throws ServiceException
     */
    public abstract DatasetRecordBean getDatasetByID(String id) throws ServiceException;

    /**
     * Searches for and returns datasets that match the passed in criteria.
     * @param updateDateFrom The update/create date to start searching from.
     * @param updateDateTo The update/create date to search until
     * @param setspec The set spec to limit the search by
     * @param offset The offset at which to start returning datasets
     * @param length The number of datasets to return
     * @return A Pair object containing the resulting datasets and an integer with the total hit count
     * that matches this criteria.
     * @throws ServiceException
     */
    public abstract Pair<List<DatasetRecordBean>,Integer> getDatasets(Date updateDateFrom,Date updateDateTo,String setspec,int offset,int length) throws ServiceException;

    /**
     * Returns a Pair object containing the list of resulting SetRecordBean objects and an integer which is the total record count for the query.
     * @param offset The offset at which to begin returning records from the resulting dataset.
     * @param length The number of records to return.
     * @return Pair object containing result list and record count.
     * @throws ServiceException
     */
    public abstract Pair<List<SetRecordBean>,Integer> getSets() throws ServiceException;
    
}
