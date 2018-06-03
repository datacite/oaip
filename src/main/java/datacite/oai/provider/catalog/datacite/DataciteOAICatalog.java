package datacite.oai.provider.catalog.datacite;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.oclc.oai.server.catalog.AbstractCatalog;
import org.oclc.oai.server.verb.BadResumptionTokenException;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.oclc.oai.server.verb.IdDoesNotExistException;
import org.oclc.oai.server.verb.NoItemsMatchException;
import org.oclc.oai.server.verb.NoMetadataFormatsException;
import org.oclc.oai.server.verb.NoSetHierarchyException;
import org.oclc.oai.server.verb.OAIInternalServerError;

import datacite.oai.provider.Constants;
import datacite.oai.provider.service.MDSSearchService;
import datacite.oai.provider.service.ServiceCollection;
import datacite.oai.provider.util.Pair;
import datacite.oai.provider.util.ResumptionToken;
import datacite.oai.provider.util.ThreadSafeSimpleDateFormat;

/**
 * Encapsulates to full OAI catalog for DataCite records from the MDS.
 * @author PaluchM
 *
 */
public class DataciteOAICatalog extends AbstractCatalog {

    private static final Logger logger = Logger.getLogger(DataciteOAICatalog.class);

    // The maximum number of entries to return for ListRecords and ListIdentifiers
    private static int maxListSize;

    // Used to parse and format date tokens
    private static final ThreadSafeSimpleDateFormat DATE_FORMAT = new ThreadSafeSimpleDateFormat(Constants.DateTime.DATETIME_FORMAT);

    public DataciteOAICatalog(Properties properties) {
        logger.info("DataciteOAICatalog initializing...");

        String maxListSizeString = properties.getProperty(Constants.Property.MDS_MAX_LIST_SIZE);
        if (maxListSizeString == null) {
            throw new IllegalArgumentException(Constants.Property.MDS_MAX_LIST_SIZE + " is missing from the properties file");
        }  else {
            maxListSize = Integer.parseInt(maxListSizeString);
        }
        
        logger.info("Done.");
    }

    /**
     * Retrieve a list of schemaLocation values associated with the specified
     * identifier.
     *
     * @param identifier the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @exception IdDoesNotExistException the specified identifier can't be found
     * @exception NoMetadataFormatsException the specified identifier was found
     * but the item is flagged as deleted and thus no schemaLocations (i.e.
     * metadataFormats) can be produced.
     */
    @SuppressWarnings("rawtypes")
    public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException,NoMetadataFormatsException,OAIInternalServerError {

        if (logger.isInfoEnabled()) {
            logger.debug("[getSchemaLocations]: identifier=" + identifier );
        }

        DatasetRecordBean dataset = null;

        // Parse identifier
        String datasetID = null;
        if (identifier != null) {
            identifier = identifier.trim();
            if (identifier.length() > 0) {
                datasetID = getRecordFactory().fromOAIIdentifier(identifier);
            }
        }

        // Get dataset
        if (datasetID != null) {
            try {
                ServiceCollection services = ServiceCollection.getInstance();
                MDSSearchService doiService = services.getMDSSearchService();
                dataset = doiService.getDatasetByID(datasetID);
            } catch (Exception e) {
                logger.error("getSchemaLocations error", e);
                throw new OAIInternalServerError(e);
            }
        }

        if (dataset == null) {
            throw new IdDoesNotExistException(identifier);
        }

        return getRecordFactory().getSchemaLocations(dataset);
    }

    /**
     * Retrieve a list of identifiers that satisfy the specified criteria
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * It may seem strange for the map to include both "headers" and "identifiers"
     * since the identifiers can be obtained from the headers. This may be true, but
     * AbstractCatalog.listRecords() can operate quicker if it doesn't
     * need to parse identifiers from the XML headers itself. Better
     * still, do like I do below and override AbstractCatalog.listRecords().
     * AbstractCatalog.listRecords() is relatively inefficient because given the list
     * of identifiers, it must call getRecord() individually for each as it constructs
     * its response. It's much more efficient to construct the entire response in one fell
     * swoop by overriding listRecords() as I've done here.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map listIdentifiers(String from,String until,String set,String metadataPrefix) throws NoItemsMatchException,OAIInternalServerError {

        if (logger.isInfoEnabled()) {
            logger.debug("[listIdentifiers]: from=" + from + ", until=" + until + ", set=" + set + ", metadataPrefix=" + metadataPrefix );
        }

        // Parse the dates
        Date fromDate = null;
        Date untilDate = null;
        try {
            fromDate = DATE_FORMAT.parse(from);
            untilDate = DATE_FORMAT.parse(until);
        } catch (Exception e) {
            logger.error("listIdentifiers error", e);
            throw new OAIInternalServerError(e);
        }
        
        if (set != null && set.equalsIgnoreCase("null")){
            set = null;
        }
                
        Pair<List<DatasetRecordBean>,Integer> results = null;

        // Get the datasets
        try {
            ServiceCollection services = ServiceCollection.getInstance();
            MDSSearchService doiService = services.getMDSSearchService();
            results = doiService.getDatasets(fromDate,untilDate,set,0,maxListSize );
        } catch (Exception e) {
            logger.error("listIdentifiers error", e);
            throw new OAIInternalServerError(e);
        }

        List<DatasetRecordBean> datasets = results.getFirst();
        int numberOfHits = results.getSecond();

        if (datasets == null || datasets.isEmpty()) {
            throw new NoItemsMatchException();
        }

        if (logger.isDebugEnabled()){
            logger.debug("Return size: "+datasets.size()+" # of hits: "+numberOfHits);
        }
        
        // OAI response
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

        for (DatasetRecordBean dataset : datasets) {
            String[] header = getRecordFactory().createHeader(dataset);
            headers.add(header[0]);
            identifiers.add(header[1]);
        }

        // Add resumption token as necessary
        if (datasets.size() < numberOfHits) {

            String tokenId = getTokenId();

            ResumptionToken token = new ResumptionToken();
            token.setId(tokenId);
            token.setFromDate(from);
            token.setUntilDate(until);
            token.setRecordCount(datasets.size());
            token.setSet(set);
            token.setPrefix(metadataPrefix);

            listIdentifiersMap.put("resumptionMap", getResumptionMap(token.toString(),numberOfHits,0));
        }
        else{
        	//no resumption token but still need to populate completeListSize and cursor values.
            listIdentifiersMap.put("resumptionMap",getResumptionMap("",numberOfHits,0));        	
        }
        

        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());

        return listIdentifiersMap;
    }

    /**
     * Retrieve the next set of identifiers associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listIdentifiers() Map result.
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * @exception BadResumptionTokenException the value of the resumptionToken
     * is invalid or expired.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException,OAIInternalServerError {

        if (logger.isInfoEnabled()) {
            logger.debug("[listIdentifiers]: resumptionToken=" + resumptionToken);
        }

        if (resumptionToken == null) {
            throw new BadResumptionTokenException();
        }

        // Parse the incoming resumption token.
        ResumptionToken token = new ResumptionToken(resumptionToken);
        String from = token.getFromDate();
        String until = token.getUntilDate();
        String set = token.getSet();
        String metadataPrefix = token.getPrefix();
        int previousRecordCount = token.getRecordCount();
        token = null;

        // Parse the dates
        Date fromDate = null;
        Date untilDate = null;
        try {
            fromDate = DATE_FORMAT.parse( from );
            untilDate = DATE_FORMAT.parse( until );
        } catch (Exception e) {
            logger.error("listIdentifiers error", e);
            throw new BadResumptionTokenException();
        }

        if (set != null && set.equalsIgnoreCase("null")){
            set = null;
        }        
        
        Pair<List<DatasetRecordBean>,Integer> results = null;

        // Get the datasets
        try {
            ServiceCollection services = ServiceCollection.getInstance();
            MDSSearchService doiService = services.getMDSSearchService();
            results = doiService.getDatasets( fromDate, untilDate, set, previousRecordCount, maxListSize );
        } catch (Exception e) {
            logger.error("listIdentifiers error", e);
            throw new OAIInternalServerError(e);
        }

        List<DatasetRecordBean> datasets = results.getFirst();
        int numberOfHits = results.getSecond();

        if (datasets == null || datasets.isEmpty()) {
            throw new BadResumptionTokenException("No identifiers found for resumption token");
        }

        if (logger.isDebugEnabled()){
            logger.debug("Return size: "+datasets.size()+" # of hits: "+numberOfHits);
        }
        
        // OAI response
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

        for (DatasetRecordBean dataset : datasets) {
            String[] header = getRecordFactory().createHeader(dataset);
            headers.add(header[0]);
            identifiers.add(header[1]);
        }

        // Add resumption token as necessary
        if (previousRecordCount + datasets.size() < numberOfHits) {

            String tokenId = getTokenId();

            ResumptionToken nextToken = new ResumptionToken();
            nextToken.setId(tokenId);
            nextToken.setFromDate(from);
            nextToken.setUntilDate(until);
            nextToken.setRecordCount(previousRecordCount + datasets.size());
            nextToken.setSet(set);
            nextToken.setPrefix(metadataPrefix);

            listIdentifiersMap.put("resumptionMap", getResumptionMap(nextToken.toString(),numberOfHits,previousRecordCount));
        }
        else{
        	//no resumption token but still need to populate completeListSize and cursor values.
            listIdentifiersMap.put("resumptionMap",getResumptionMap("",numberOfHits,previousRecordCount));        	
        }
        

        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());

        return listIdentifiersMap;
    }

    /**
     * Retrieve the specified metadata for the specified identifier
     *
     * @param identifier the OAI identifier
     * @param metadataPrefix the OAI metadataPrefix
     * @return the <record/> portion of the XML response.
     * @exception CannotDisseminateFormatException the metadataPrefix is not
     * supported by the item.
     * @exception IdDoesNotExistException the identifier wasn't found
     */
    public String getRecord(String identifier, String metadataPrefix) throws CannotDisseminateFormatException,IdDoesNotExistException,OAIInternalServerError {

        if (logger.isInfoEnabled()) {
            logger.debug("[getRecord]: identifier=" + identifier +", metadataPrefix=" + metadataPrefix);
        }

        DatasetRecordBean dataset = null;

        // Parse identifier
        String datasetID = null;
        if (identifier != null) {
            identifier = identifier.trim();
            if (identifier.length() > 0) {
                datasetID = getRecordFactory().fromOAIIdentifier(identifier);
            }
        }

        // Get dataset
        if (datasetID != null) {
            try {
                ServiceCollection services = ServiceCollection.getInstance();
                MDSSearchService doiService = services.getMDSSearchService();
                dataset = doiService.getDatasetByID(datasetID);
            } catch (Exception e) {
                logger.error("getSchemaLocations error", e);
                throw new OAIInternalServerError(e);
            }
        }

        if (dataset == null) {
            throw new IdDoesNotExistException(identifier);
        }

        return constructRecord(dataset, metadataPrefix);
    }

    /**
     * Retrieve a list of records that satisfy the specified criteria. Note, though,
     * that unlike the other OAI verb type methods implemented here, both of the
     * listRecords methods are already implemented in AbstractCatalog rather than
     * abstracted. This is because it is possible to implement ListRecords as a
     * combination of ListIdentifiers and GetRecord combinations. Nevertheless,
     * I suggest that you override both the AbstractCatalog.listRecords methods
     * here since it will probably improve the performance if you create the
     * response in one fell swoop rather than construct it one GetRecord at a time.
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for a "records" Iterator object
     * (containing XML <record/> Strings) and an optional "resumptionMap" Map.
     * @exception CannotDisseminateFormatException the metadataPrefix isn't
     * supported by the item.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map listRecords(String from,String until,String set,String metadataPrefix) throws CannotDisseminateFormatException,NoItemsMatchException,OAIInternalServerError {

        if (logger.isInfoEnabled()) {
            logger.debug("[listRecords]: from=" + from + ", until=" + until + ", set=" + set + ", metadataPrefix=" + metadataPrefix );
        }

        // Parse the dates
        Date fromDate = null;
        Date untilDate = null;
        try {
            fromDate = DATE_FORMAT.parse( from );
            untilDate = DATE_FORMAT.parse( until );
        } catch (Exception e) {
            logger.error("listRecords error", e);
            throw new OAIInternalServerError(e);
        }
        
        if (set != null && set.equalsIgnoreCase("null")){
            set = null;
        }        
        
        Pair<List<DatasetRecordBean>,Integer> results = null;

        // Get the datasets
        try {
            ServiceCollection services = ServiceCollection.getInstance();
            MDSSearchService doiService = services.getMDSSearchService();
            results = doiService.getDatasets( fromDate, untilDate,set, 0, maxListSize );
        } catch (Exception e) {
            logger.error("listRecords error", e);
            throw new OAIInternalServerError(e);
        }

        List<DatasetRecordBean> datasets = results.getFirst();
        int numberOfHits = results.getSecond();

        if (datasets == null || datasets.isEmpty()) {
            throw new NoItemsMatchException();
        }

        if (logger.isDebugEnabled()){
            logger.debug("Return size: "+datasets.size()+" # of hits: "+numberOfHits);
        }
        
        // OAI response
        Map listRecordsMap = new HashMap();
        ArrayList records = new ArrayList();

        for (DatasetRecordBean dataset : datasets) {
            String record = constructRecord(dataset, metadataPrefix);
            records.add(record);
        }

        // Add resumption token as necessary
        if (datasets.size() < numberOfHits) {

            String tokenId = getTokenId();

            ResumptionToken token = new ResumptionToken();
            token.setId(tokenId);
            token.setFromDate(from);
            token.setUntilDate(until);
            token.setRecordCount(datasets.size());
            token.setSet(set);
            token.setPrefix(metadataPrefix);

            listRecordsMap.put("resumptionMap", getResumptionMap(token.toString(),numberOfHits,0));
        }
        else{
        	//no resumption token but still need to populate completeListSize and cursor values.
            listRecordsMap.put("resumptionMap",getResumptionMap("",numberOfHits,0));        	
        }        

        listRecordsMap.put("records", records.iterator());

        return listRecordsMap;
    }

    /**
     * Retrieve the next set of records associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listRecords() Map result.
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * @exception BadResumptionTokenException the value of the resumptionToken argument
     * is invalid or expired.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map listRecords(String resumptionToken) throws BadResumptionTokenException,OAIInternalServerError {

        if (logger.isInfoEnabled()) {
            logger.debug("[listRecords]: resumptionToken=" + resumptionToken);
        }

        if (resumptionToken == null) {
            throw new BadResumptionTokenException();
        }

        // Parse the incoming resumption token.
        ResumptionToken token = new ResumptionToken(resumptionToken);
        String from = token.getFromDate();
        String until = token.getUntilDate();
        String set = token.getSet();
        String metadataPrefix = token.getPrefix();
        int previousRecordCount = token.getRecordCount();
        token = null;

        // Parse the dates
        Date fromDate = null;
        Date untilDate = null;
        try {
            fromDate = DATE_FORMAT.parse( from );
            untilDate = DATE_FORMAT.parse( until );
        } catch (Exception e) {
            logger.error("listRecords error", e);
            throw new BadResumptionTokenException();
        }

        if (set != null && set.equalsIgnoreCase("null")){
            set = null;
        }        
                
        Pair<List<DatasetRecordBean>,Integer> results = null;

        // Get the datasets
        try {
            ServiceCollection services = ServiceCollection.getInstance();
            MDSSearchService doiService = services.getMDSSearchService();
            results = doiService.getDatasets( fromDate, untilDate, set, previousRecordCount, maxListSize );
        } catch (Exception e) {
            logger.error("listRecords error", e);
            throw new OAIInternalServerError(e);
        }

        List<DatasetRecordBean> datasets = results.getFirst();
        int numberOfHits = results.getSecond();

        if (datasets == null || datasets.isEmpty()) {
            throw new BadResumptionTokenException("No records found for resumption token");
        }

        if (logger.isDebugEnabled()){
            logger.debug("Return size: "+datasets.size()+" # of hits: "+numberOfHits);
        }
        
        // OAI response
        Map listRecordsMap = new HashMap();
        ArrayList records = new ArrayList();

        try {
            for (DatasetRecordBean dataset : datasets) {
                String record = constructRecord(dataset, metadataPrefix);
                records.add(record);
            }
        } catch(CannotDisseminateFormatException e) {
            // MetadataPrefix in resumption token was hacked
            throw new BadResumptionTokenException();
        }

        // Add resumption token as necessary
        if (previousRecordCount + datasets.size() < numberOfHits) {

            String tokenId = getTokenId();

            ResumptionToken nextToken = new ResumptionToken();
            nextToken.setId(tokenId);
            nextToken.setFromDate(from);
            nextToken.setUntilDate(until);
            nextToken.setRecordCount(previousRecordCount + datasets.size());
            nextToken.setSet(set);
            nextToken.setPrefix(metadataPrefix);

            listRecordsMap.put("resumptionMap", getResumptionMap(nextToken.toString(),numberOfHits,previousRecordCount));
        }
        else{
        	//no resumption token but still need to populate completeListSize and cursor values.
            listRecordsMap.put("resumptionMap",getResumptionMap("",numberOfHits,previousRecordCount));        	
        }
        

        listRecordsMap.put("records", records.iterator());

        return listRecordsMap;
    }

    /**
     * Utility method to construct a Record object for a specified
     * metadataFormat from a native record
     *
     * @param nativeItem native item from the dataase
     * @param metadataPrefix the desired metadataPrefix for performing the crosswalk
     * @return the <record/> String
     * @exception CannotDisseminateFormatException the record is not available
     * for the specified metadataPrefix.
     */
    private String constructRecord(Object nativeItem, String metadataPrefix) throws CannotDisseminateFormatException {

        String schemaURL = null;
        if (metadataPrefix != null) {
            schemaURL = getCrosswalks().getSchemaURL(metadataPrefix);
            if (schemaURL == null) {
                throw new CannotDisseminateFormatException(metadataPrefix);
            }
        }

        return getRecordFactory().create(nativeItem, schemaURL, metadataPrefix);
    }

    /**
     * Retrieve a list of sets that satisfy the specified criteria
     *
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception OAIBadRequestException signals an http status code 400 problem
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map listSets() throws NoSetHierarchyException, OAIInternalServerError{

        if (logger.isInfoEnabled()) {
            logger.debug("[listSets]");
        }
        Pair<List<SetRecordBean>,Integer> results = null;
        
        try {
            MDSSetCache cache = MDSSetCache.getInstance();            
            results = cache.getSets(0, maxListSize);
        } catch (Exception e) {
            logger.error("listSets error", e);
            throw new OAIInternalServerError(e);
        }
        
        List<SetRecordBean> setlist = results.getFirst();
        int numberOfHits = results.getSecond();
        
        if (setlist == null || setlist.isEmpty()) {
            throw new NoSetHierarchyException();
        }

        if (logger.isDebugEnabled()){
            logger.debug("Return size: "+setlist.size()+" # of hits: "+numberOfHits);
        }    
        
        // OAI response
        Map listSetsMap = new HashMap();
        ArrayList sets = new ArrayList();
        
        for (SetRecordBean set : setlist){
            sets.add(set.getSetSpec());
        }

        // Add resumption token as necessary with completeListSize and cursor values.
        if (setlist.size() < numberOfHits) {
            String tokenId = getTokenId();

            ResumptionToken token = new ResumptionToken();
            token.setId(tokenId);
            token.setRecordCount(setlist.size());
            token.setFromDate(null);
            token.setUntilDate(null);
            token.setPrefix(null);
            token.setSet(null);
           
            listSetsMap.put("resumptionMap",getResumptionMap(token.toString(),numberOfHits,0));
        }
        else{
        	//no resumption token but still need to populate completeListSize and cursor values.
        	listSetsMap.put("resumptionMap",getResumptionMap("",numberOfHits,0));
        }

        listSetsMap.put("sets", sets.iterator());

        return listSetsMap;
    }

    /**
     * Retrieve the next set of sets associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listSets() Map result.
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception BadResumptionTokenException the value of the resumptionToken
     * is invalid or expired.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map listSets(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {

        if (logger.isInfoEnabled()) {
            logger.debug("[listSets]: resumptionToken=" + resumptionToken);
        }

        if (resumptionToken == null) {
            throw new BadResumptionTokenException();
        }

        // Parse the incoming resumption token.
        ResumptionToken token = new ResumptionToken(resumptionToken);
        int previousRecordCount = token.getRecordCount();
        token = null;

        Pair<List<SetRecordBean>,Integer> results = null;
        
        //get datacentres
        try {
            MDSSetCache cache = MDSSetCache.getInstance();
            results = cache.getSets(previousRecordCount, maxListSize);
        } catch (Exception e) {
            logger.error("listSets error", e);
            throw new OAIInternalServerError(e);
        }
                
        if (results == null){ //something not right with parameters passed in (i.e. too big or too small)
        	throw new BadResumptionTokenException("No sets found for resumption token");
        }

        List<SetRecordBean> setlist = results.getFirst();
        int numberOfHits = results.getSecond();        
        
        if (setlist == null || setlist.isEmpty()) {
        	throw new BadResumptionTokenException("No sets found for resumption token");
        }

        if (logger.isDebugEnabled()){
            logger.debug("Return size: "+setlist.size()+" # of hits: "+numberOfHits);
        }
              
        // OAI response
        Map listSetsMap = new HashMap();
        ArrayList sets = new ArrayList();

        for (SetRecordBean set : setlist){
            sets.add(set.getSetSpec());
        }

        // Add resumption token as necessary
        if ((previousRecordCount + setlist.size()) < numberOfHits) {
            String tokenId = getTokenId();

            ResumptionToken nextToken = new ResumptionToken();
            nextToken.setId(tokenId);
            nextToken.setRecordCount(previousRecordCount + setlist.size());
            nextToken.setFromDate(null);
            nextToken.setUntilDate(null);
            nextToken.setPrefix(null);
            nextToken.setSet(null);

            listSetsMap.put("resumptionMap",getResumptionMap(nextToken.toString(),numberOfHits,previousRecordCount));
        }
        else{
        	//no resumption token but still need to populate completeListSize and cursor values.
            listSetsMap.put("resumptionMap",getResumptionMap("",numberOfHits,previousRecordCount));        	
        }

        listSetsMap.put("sets", sets.iterator());

        return listSetsMap;
    }

    /**
     * close the repository
     */
    public void close() {
        // Nothing to do
    }

    /**
     * Use the current date as the basis for the resumptiontoken
     *
     * @return a String version of the current time
     */
    private static String getTokenId() {
        return String.valueOf( System.currentTimeMillis() );
    }
}
