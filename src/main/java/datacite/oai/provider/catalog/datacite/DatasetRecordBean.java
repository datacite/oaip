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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import datacite.oai.provider.Constants;

/**
 * Encapsulates a single DataCite record in the MDS. 
 * @author PaluchM
 *
 */
public class DatasetRecordBean implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public static final String DEFAULT_SCHEMA_VERSION = Constants.SchemaVersion.VERSION_2_0;    
    /**
     * DataCite Schema version map
     * key = schema version indicator string in metadata (i.e. "datacite-metadata-v2.0.xsd")
     * value = schema version number as a string (i.e. "2.1")
     */
    public static final HashMap<String,String> versionMap = new HashMap<String,String>();
    static{
        versionMap.put("/schema/kernel-2.1",Constants.SchemaVersion.VERSION_2_1);
        versionMap.put("/schema/namespace",Constants.SchemaVersion.VERSION_2_1);
        versionMap.put("/meta/kernel-2.0", Constants.SchemaVersion.VERSION_2_0);
        versionMap.put("datacite-metadata-v2.0.xsd",Constants.SchemaVersion.VERSION_2_0);        
    }
    
    
    private String id;
    private String metadata;
    private Date updateDate;
    
    private String symbol;
    private boolean refQuality;
    private boolean isActive;
    
    private String schemaVersion;

    /**
     * Public constructor.
     * @param id
     * @param metadata
     * @param updateDate
     * @param refQuality
     * @param isActive
     * @param symbol
     */
    public DatasetRecordBean(String id,String metadata,Date updateDate,boolean refQuality,boolean isActive,String symbol){
        this.id = id;        
        this.updateDate = updateDate;
        this.refQuality = refQuality;
        this.isActive = isActive;
        this.symbol = symbol;
        setMetadata(fixMetadata(metadata));
    }

    public String getId() {
        return this.id;
    }

    public void setMetadata(String metadata){
        //remove xml declaration and comment blocks
        this.metadata = metadata.replaceAll("(<!--.*-->)","").replaceAll("(<\\?xml.*\\?>)","");
        
        setSchemaVersion(determineSchemaVersion(this.metadata));
    }
    
    public String getMetadata() {
        return this.metadata;
    }

    public Date getUpdateDate() { 
        return this.updateDate;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setRefQuality(boolean refQuality) {
        this.refQuality = refQuality;
    }

    public boolean isRefQuality() {
        return refQuality;
    }

    /**
     * Sets the DataCite schema version that this record adheres to
     * @param schemaVersion
     */
    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * Returns the DataCite schema version that this record adheres to
     * @return
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Sets the active/deleted flag for this record.
     * @param isActive
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Returns the active/deleted flag for this record.
     * @return
     */
    public boolean isActive() {
        return isActive;
    }    
    
    /**
     * Creates a set listing for this record. 
     * @return an array containing all of the 'sets' that this record is a part of.
     */
    public List<String> getSetList(){
        String symbol = getSymbol();
        LinkedList<String> sets = new LinkedList<String>();
        
        if (symbol != null && symbol.trim().length()>0){
            sets.add(symbol);
            if (isRefQuality()){
                sets.add((symbol+Constants.Set.REF_QUALITY_SUFFIX).toUpperCase());
            }
        }
        
        while (symbol.contains(".")){
            symbol = symbol.substring(0,symbol.lastIndexOf("."));
            if (isRefQuality()){
                sets.add((symbol+Constants.Set.REF_QUALITY_SUFFIX).toUpperCase());
            }
            sets.add(0,symbol);
        }
        
        return sets;
    }
        
    
    //TODO: remove this
    /**
     * Temporary fix for metadata with bad chars in MDS (\012 \011 \015 etc.).
     * @param metadata
     * @return
     */
    private String fixMetadata(String metadata){
        metadata = metadata.replaceAll("((\\\\[0-9]{3})+)","");                
        return metadata;
    }
    

    /**
     * Attempts to determine the schema version that this record adheres to.
     * @param metadata
     * @return
     */
    private String determineSchemaVersion(String metadata){
        metadata = metadata.toLowerCase();
        
        for (String key : versionMap.keySet()){
            if (metadata.contains(key.toLowerCase())){
                return versionMap.get(key);
            }
        }
        
        // default version
        return DEFAULT_SCHEMA_VERSION;
    }


    
    
}