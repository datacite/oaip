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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.oclc.oai.server.catalog.RecordFactory;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;

import datacite.oai.provider.Constants;
import datacite.oai.provider.util.ThreadSafeSimpleDateFormat;

/**
 * Encapsulates OAI record creation functionality for DataCite records in MDS. 
 * @author PaluchM
 *
 */
public class DataciteOAIRecordFactory extends RecordFactory {

    private static final Logger logger = Logger.getLogger(DataciteOAIRecordFactory.class);
    private static final ThreadSafeSimpleDateFormat DATE_FORMAT = new ThreadSafeSimpleDateFormat(Constants.DateTime.DATETIME_FORMAT);
    private String repositoryIdentifier;

    /**
     * Public constructor
     * @param properties
     */
    public DataciteOAIRecordFactory(Properties properties) {
        super(properties);

        this.repositoryIdentifier = properties.getProperty("DataciteOAIRecordFactory.repositoryIdentifier");
        if (this.repositoryIdentifier == null) {
            throw new IllegalArgumentException("DataciteOAIRecordFactory.repositoryIdentifier is missing from the properties file");
        }
    }

    @Override
    public String fromOAIIdentifier(String oaiIdentifier) {
        StringTokenizer tokenizer = new StringTokenizer(oaiIdentifier, ":");
        try {
            tokenizer.nextToken();
            tokenizer.nextToken();
            return tokenizer.nextToken();
        } catch (java.util.NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) throws IllegalArgumentException, CannotDisseminateFormatException {
        // TODO: Not Implemented
        return null;
    }

    
    @Override   
    public String getOAIIdentifier(Object nativeItem) {
        return "oai:" + this.repositoryIdentifier + ":"+((DatasetRecordBean)nativeItem).getId();
    }

    @Override
    public String getDatestamp(Object nativeItem) {

        DatasetRecordBean metadataBean = (DatasetRecordBean)nativeItem;

        if (metadataBean.getUpdateDate() != null){
            return DATE_FORMAT.format(metadataBean.getUpdateDate());
        } else {
            return "";
        }
    }

    @Override
    public Iterator<String> getSetSpecs(Object nativeItem) throws IllegalArgumentException {

        List<String> list = new ArrayList<String>();

        try{
            list.addAll(((DatasetRecordBean)nativeItem).getSetList());
        }catch(Exception e){
            logger.error("Error getting set specs for item: nativeItem");
        }
        
        return list.iterator();
    }

    @Override
    public boolean isDeleted(Object nativeItem) {
        return !((DatasetRecordBean)nativeItem).isActive();
    }

    @Override
    public Iterator getAbouts(Object nativeItem) {
        // TODO: Not Implemented
        return null;
    }
}
