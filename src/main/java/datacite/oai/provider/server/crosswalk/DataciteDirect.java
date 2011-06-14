package datacite.oai.provider.server.crosswalk;

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

import java.util.Properties;

import org.oclc.oai.server.crosswalk.Crosswalk;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.oclc.oai.server.verb.OAIInternalServerError;

import datacite.oai.provider.catalog.datacite.DatasetRecordBean;

public class DataciteDirect extends Crosswalk {

    public DataciteDirect(Properties properties) throws OAIInternalServerError {
        super("http://datacite.org/schema/nonexistant http://schema.datacite.org/meta/nonexistant/nonexistant.xsd");
    }

    public DataciteDirect(String schema, Properties properties) throws OAIInternalServerError {
        super("http://datacite.org/schema/nonexistant http://schema.datacite.org/meta/nonexistant/nonexistant.xsd");
    }
        
    @Override
    public boolean isAvailableFor(Object nativeItem) {
        return nativeItem instanceof DatasetRecordBean;
    }

    @Override
    public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
        DatasetRecordBean dataset = (DatasetRecordBean)nativeItem;
        String result = dataset.getMetadata();

        return result;
    }
}
