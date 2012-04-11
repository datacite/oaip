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

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.oclc.oai.server.crosswalk.Crosswalk;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.oclc.oai.server.verb.OAIInternalServerError;

import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.util.XMLUtil;

/**
 * Encapsulates the creation of metadata in oai_datacite format.
 * @author PaluchM
 */
public class OaiDatacite extends Crosswalk {

	
	/** The metadata prefix of this result format*/
	public static final String METADATA_PREFIX = "oai_datacite";
	
	private final static String schemaNamespace = "http://schema.datacite.org/oai/oai-1.0/";
    private final static String schemaLocation = "http://schema.datacite.org/oai/oai-1.0/oai.xsd";
    

    
    private final String rootElement = "oai_datacite";
    private final String versionElement = "schemaVersion";
    private final String symbolElement = "datacentreSymbol";
    private final String rqElement = "isReferenceQuality";
    private final String payloadElement = "payload";
    
    /**
     * Public constructor
     * @param properties
     * @throws OAIInternalServerError
     */
    public OaiDatacite(Properties properties) throws OAIInternalServerError {
        super(schemaNamespace+" "+schemaLocation);
    }

    /**
     * Public constructor
     * @param properties
     * @throws OAIInternalServerError
     */
    public OaiDatacite(String schema, Properties properties) throws OAIInternalServerError {
        super(schemaNamespace+" "+schemaLocation);
    }
        
    @Override
    public boolean isAvailableFor(Object nativeItem) {
        return nativeItem instanceof DatasetRecordBean;
    }

    @Override
    public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
        try{
        	DatasetRecordBean dataset = (DatasetRecordBean)nativeItem;
        	return buildDocument(dataset);
        }
        catch(Exception e){
        	throw (CannotDisseminateFormatException) new CannotDisseminateFormatException(METADATA_PREFIX).initCause(e);
        }
    }
    
    /**
     * Builds an oai_datacite format representation of a record.
     * @param rec The record
     * @return XML metadata in oai_datacite format.
     */
    private String buildDocument(DatasetRecordBean rec) throws UnsupportedEncodingException{        
        StringBuilder doc = new StringBuilder();
        String[] attribs = new String[]{"xmlns=\""+schemaNamespace+"\"","xsi:schemaLocation=\""+schemaNamespace+" "+schemaLocation+"\""};
        
        String metadata = XMLUtil.toXMLString(rec.getMetadata(),"UTF-8");
        
        doc.append(openTagWithAttrib(rootElement,attribs));        
            doc.append(openTag(rqElement));
                doc.append(rec.isRefQuality()?"true":"false");
            doc.append(closeTag(rqElement));
            doc.append(openTag(versionElement));
                doc.append(rec.getSchemaVersion());
            doc.append(closeTag(versionElement));
            doc.append(openTag(symbolElement));
                doc.append(StringEscapeUtils.escapeXml(rec.getSymbol()));
            doc.append(closeTag(symbolElement));
            doc.append(openTag(payloadElement));
                doc.append(metadata);
            doc.append(closeTag(payloadElement));
        doc.append(closeTag(rootElement));
        
        return doc.toString();
    }
    
    /**
     * Creates an open XML tag for element "name".
     * @param name Element name.
     * @return xml tag.
     */
    private String openTag(String name){
        return "<"+name+">";
    }
    
    /**
     * Creates an open XML tag for element "name" with attributes "attribs".
     * @param name Element name.
     * @param attribs A string array of full attributes e.g. 'xmlns=\"http://....\"'
     * @return 
     */
    private String openTagWithAttrib(String name,String[] attribs){
        StringBuilder sb = new StringBuilder("<");
        sb.append(name);
        if (attribs!=null){
            for (String attrib : attribs){
                sb.append(" ");
                sb.append(attrib);
            }
        }
        sb.append(">");
        return sb.toString();
    }
    
    /**
     * Creates a closing XML tag for element "name".
     * @param name Element name.
     * @return xml tag
     */
    private String closeTag(String name){
        return "</"+name+">";
    }
}
