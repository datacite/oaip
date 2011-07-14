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

import org.apache.log4j.Logger;
import org.oclc.oai.server.crosswalk.Crosswalk;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.oclc.oai.server.verb.OAIInternalServerError;

import datacite.oai.provider.Constants;
import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.service.ServiceCollection;
import datacite.oai.provider.service.ServiceException;
import datacite.oai.provider.service.TransformerService;

/**
 * Class encapsulating the dissemination of records in oai_dc format. 
 * @author PaluchM
 *
 */
public class Datacite2Oai_dc extends Crosswalk{

    private static final Logger logger = Logger.getLogger(Datacite2Oai_dc.class);

    public Datacite2Oai_dc(Properties properties) throws OAIInternalServerError {
        super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
    }

    public Datacite2Oai_dc(String schema, Properties properties) throws OAIInternalServerError {
        super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
    }

    @Override
    public boolean isAvailableFor(Object nativeItem) {
        return nativeItem instanceof DatasetRecordBean;
    }

    @Override
    public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {

        String result = null;

        try {
            DatasetRecordBean dataset = (DatasetRecordBean)nativeItem;

            ServiceCollection services = ServiceCollection.getInstance();
            TransformerService transformerService = services.getTransformerService();
            
            if (dataset.getSchemaVersion().equalsIgnoreCase(Constants.SchemaVersion.VERSION_2_2)){
                result = transformerService.doTransform_Kernel2_2ToOaidc(dataset.getMetadata());
            }
            else if (dataset.getSchemaVersion().equalsIgnoreCase(Constants.SchemaVersion.VERSION_2_1)){
                result = transformerService.doTransform_Kernel2_1ToOaidc(dataset.getMetadata());
            }
            else{
                result = transformerService.doTransform_Kernel2_0ToOaidc(dataset.getMetadata());
            }           
        } 
        catch(ServiceException e) {
            logger.error("Error transforming dataset", e);
            throw new CannotDisseminateFormatException(e.toString());
        }

        return result;
    }
}
