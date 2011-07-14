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
public class MDSSearchService extends Service {

    private static final Logger logger = Logger.getLogger(MDSSearchService.class);    
    private PooledDataSource pds = null;
    private String[] omitPrefixes = null;
    
    /**
     * Public constructor.
     * @param servletContext
     * @throws ServiceException
     */
    public MDSSearchService(ServletContext servletContext) throws ServiceException {
        super(servletContext);

        try {
            ApplicationContext context = ApplicationContext.getInstance(); 

            String url = context.getProperty(Constants.Database.MDS_DB_URL);
            String driver = context.getProperty(Constants.Database.MDS_DB_DRIVER);
            String username = context.getProperty(Constants.Database.MDS_DB_USERNAME);
            String password = context.getProperty(Constants.Database.MDS_DB_PASSWORD);
            
            
            int poolStartSize = context.getIntProperty(Constants.Database.MDS_DB_POOL_START);
            int poolMaxSize = context.getIntProperty(Constants.Database.MDS_DB_POOL_MAX);
                        
            pds = new PooledDataSource(driver,url,username, password,poolStartSize,poolMaxSize);
            
            String omit = context.getProperty(Constants.Property.MDS_PREFIXES_OMIT); 
            if (omit!=null && omit.trim().length()>0){
                omitPrefixes = omit.split(",");
            }                        
        } 
        catch (PropertyNotFoundException pe) {
            throw new ServiceException(pe);
        }
        catch(Exception e){
            throw new ServiceException(e);
        }        
    }

    @Override
    public void destroy() {
        if (pds!=null){
            pds.close();
        }
    }

    /**
     * Returns a dataset for the given id.
     * @param id
     * @return The dataset for the associated id or null if not found.
     * @throws ServiceException
     */
    public DatasetRecordBean getDatasetByID(String id) throws ServiceException {
        DatasetRecordBean dataset = null;
        
        StringBuilder query = new StringBuilder("select d.id as dataset_id,d.is_ref_quality,d.is_active,DATE_FORMAT(GREATEST(m.created,d.updated),'");
        query.append(Constants.DateTime.DATETIME_FORMAT_MYSQL);
        query.append("') as update_date,m.xml,dc.symbol ");
        query.append("from dataset d, metadata m, datacentre dc where m.dataset = d.id and m.metadata_version = (select max(m.metadata_version) from metadata m where m.dataset = d.id) and d.datacentre = dc.id and d.id = ?");

        //add doi prefixes to omit from results
        if (omitPrefixes!=null && omitPrefixes.length>0){
            for (String omit : omitPrefixes){
                query.append(" and d.doi not like '"+omit+"%'");
            }
        }        
        
        if (logger.isDebugEnabled()){
            logger.debug("Query: "+query.toString());
            logger.debug("ID: "+id);
        }
        
        try{            
            dataset = (DatasetRecordBean)pds.runQuery(query.toString(),new String[]{id},new DatasetRowMapper());
        } catch(Exception e) {
            throw new ServiceException("getDatasetByID threw exception",e);
        }
        return dataset;
    }

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
    public Pair<List<DatasetRecordBean>,Integer> getDatasets(Date updateDateFrom,Date updateDateTo,String setspec,int offset,int length) throws ServiceException {
        ThreadSafeSimpleDateFormat df = new ThreadSafeSimpleDateFormat(Constants.DateTime.DATETIME_FORMAT);
        
        StringBuilder listquery = new StringBuilder("select d.id as dataset_id,d.is_ref_quality,d.is_active,DATE_FORMAT(GREATEST(m.created,d.updated),'");
        listquery.append(Constants.DateTime.DATETIME_FORMAT_MYSQL);
        listquery.append("') as update_date,m.xml,dc.symbol ");
        
        StringBuilder countquery = new StringBuilder("select count(d.id) as count ");
                
        listquery.append("from dataset d, metadata m, datacentre dc where m.dataset = d.id ");         
        countquery.append("from dataset d, metadata m, datacentre dc where m.dataset = d.id ");
        
        listquery.append("and m.metadata_version = (select max(m.metadata_version) from metadata m where m.dataset = d.id) and d.datacentre = dc.id");
        countquery.append("and m.metadata_version = (select max(m.metadata_version) from metadata m where m.dataset = d.id) and d.datacentre = dc.id");
        
        
        //hold multiple possible where clauses.
        LinkedList<String> ands = new LinkedList<String>();        
        
        //add the requested set spec
        if (setspec !=null && setspec.trim().length()>0){
            setspec = setspec.trim().toUpperCase();
            
            if (!setspec.equalsIgnoreCase(Constants.Set.REF_QUALITY)){                
                if (setspec.endsWith(Constants.Set.REF_QUALITY_SUFFIX)){
                    ands.add("d.is_ref_quality = TRUE");
                    setspec = setspec.substring(0,setspec.lastIndexOf(Constants.Set.REF_QUALITY_SUFFIX));            
                }
                if(setspec.contains(".")){// datacentre symbol
                    ands.add("UPPER(dc.symbol) = '"+setspec+"'");    
                }
                else{ //allocator symbol
                    ands.add("UPPER(dc.symbol) like '"+setspec+".%'");
                }
            }
            else{//refquality set
                setspec = null;
                ands.add("d.is_ref_quality = TRUE");                
            }
        }
        
        //if dates are present
        if (updateDateFrom != null){
            ands.add("DATE_FORMAT(GREATEST(m.created,d.updated),'"+Constants.DateTime.DATETIME_FORMAT_MYSQL+"') >= '"+df.format(updateDateFrom)+"'");
        }
        if (updateDateTo != null){
            ands.add("DATE_FORMAT(GREATEST(m.created,d.updated),'"+Constants.DateTime.DATETIME_FORMAT_MYSQL+"') <= '"+df.format(updateDateTo)+"'");
        }

        //add doi prefixes to omit from results
        if (omitPrefixes!=null && omitPrefixes.length>0){
            for (String omit : omitPrefixes){
                ands.add("d.doi not like '"+omit+"%'");
            }
        }
        
        //combine list query and clauses
        listquery = addClauses(listquery,ands,"and");
        listquery.append("order by update_date desc LIMIT "+offset+","+length);
        
        if (logger.isDebugEnabled()){
            logger.debug("List Query: "+listquery.toString());
        }

        //combine count query and clauses
        countquery = addClauses(countquery,ands,"and");
        
        if (logger.isDebugEnabled()){
            logger.debug("Count Query: "+countquery.toString());
        }

        LinkedList<DatasetRecordBean> results = new LinkedList<DatasetRecordBean>();
        int count;
        
        try{            
            count = Integer.valueOf(pds.runQueryForString(countquery.toString(),1));            
            Object[] objs = pds.runQueryForList(listquery.toString(),new DatasetRowMapper());
            
            if (objs != null){
                for (Object obj : objs){
                    results.add((DatasetRecordBean)obj);
                }
            }            
        } 
        catch(Exception e) {
            throw (ServiceException)new ServiceException("getDatasets threw exception",e);
        }

        return new Pair<List<DatasetRecordBean>,Integer>(results,count);
    }

    /**
     * Returns a Pair object containing the list of resulting SetRecordBean objects and an integer which is the total record count for the query.
     * @param offset The offset at which to begin returning records from the resulting dataset.
     * @param length The number of records to return.
     * @return Pair object containing result list and record count.
     * @throws ServiceException
     */
    public Pair<List<SetRecordBean>,Integer> getSets() throws ServiceException {
        return getSets(-1,-1);
    }
    
    /**
     * Returns a Pair object containing the list of resulting SetRecordBean objects and an integer which is the total record count for the query.
     * @param offset The offset at which to begin returning records from the resulting dataset.
     * @param length The number of records to return.
     * @return Pair object containing result list and record count.
     * @throws ServiceException
     */
    public Pair<List<SetRecordBean>,Integer> getSets(int offset,int length) throws ServiceException {

        String unionQuery = "(select a.symbol as symbol, a.name as name from allocator a where a.id > -1) union (select dc.symbol as symbol, dc.name as name from datacentre dc) ";
        
        //query return complete sets
        StringBuilder listQuery = new StringBuilder(unionQuery); 
        listQuery.append("order by symbol asc ");
        
        if (offset!=-1 && length!=-1){
            listQuery.append("limit "+offset+","+length);
        }
                
        if (logger.isDebugEnabled()){
            logger.debug("List query: "+listQuery.toString());
        }
        
        //query to count available sets
        StringBuilder countQuery = new StringBuilder("select count(symbol) as count from (");
        countQuery.append(unionQuery);
        countQuery.append(") tbl");
        
        logger.debug("Count query: "+countQuery.toString());
        
        LinkedList<SetRecordBean> results = new LinkedList<SetRecordBean>();
        int count;
        
        try{
            count = Integer.valueOf(pds.runQueryForString(countQuery.toString(),1));
            
            logger.debug("Record count: "+count);
            
            Object[] objs = pds.runQueryForList(listQuery.toString(),new SetRowMapper());
            
            if (objs != null){
                for (Object obj : objs){
                    results.add((SetRecordBean)obj);
                }
            }
            
            logger.info("Returned set count: "+results.size());
            
        } catch(Exception e) {
            throw (ServiceException)new ServiceException("getDatacentres threw exception").initCause(e);
        }

        return new Pair<List<SetRecordBean>,Integer>(results,count);
    }


    /*********************************************************************************************
     * PRIVATE METHODS *
     *********************************************************************************************/
    
    /**
     * Add the list of clauses to the query using the passed in operator (i.e. and, or). 
     * @param query The query up till the where clause as a StringBuilder object.
     * @param wheres The clauses to add.
     * @param operator The operator to put between each clause.
     * @return
     */
    private StringBuilder addClauses(StringBuilder query,List<String> wheres,String operator){
        for (String where : wheres){
            query.append(" ");
            query.append(operator);
            query.append(" ");
            query.append(where);
            query.append(" ");
        }
        
        return query;
    }
     
}
