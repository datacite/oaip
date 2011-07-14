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

public final class Constants {


    /**
     * Holds constants related to sets
     * @author PaluchM
     *
     */
    public static class Set {
        public static final String REF_QUALITY = "REFQUALITY";
        public static final String REF_QUALITY_SUFFIX = "."+REF_QUALITY;
    }
    
    /**
     * Holds date/time related constants.
     * @author PaluchM
     *
     */
    public static class DateTime{
        public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        public static final String DATETIME_FORMAT_MYSQL = "%Y-%m-%dT%TZ";
        
        public static final String MIN_FROM_TIME = "00:00:00";
        public static final String MIN_FROM_DATE = "0001-01-01";
        
        public static final String MAX_TO_TIME = "23:59:59";
        public static final String MAX_TO_DATE = "9999-12-31";
    }

    /**
     * Holds constants of key values in the Property(context) file. 
     * @author PaluchM
     *
     */
    public static class Property{        
        public static final String REPOSITORIES = "oai.repositories";

        public static final String ENVIRONMENT_LABEL = "environmentLabel";

        public static final String STYLESHEET_KERNEL2_0_TO_OAIDC = "stylesheet.kernel2.0_to_oaidc";
        public static final String STYLESHEET_KERNEL2_1_TO_OAIDC = "stylesheet.kernel2.1_to_oaidc";
        public static final String STYLESHEET_KERNEL2_2_TO_OAIDC = "stylesheet.kernel2.2_to_oaidc";
                
        public static final String MDS_MAX_LIST_SIZE = "DataciteOAICatalog.maxListSize";
        public static final String MDS_SETCACHE_EXPIRY_SECONDS = "mdssetcache.expiry.seconds";        
        
        public static final String DB_PROPERTIES_FILE = "db.properties";        
        
        public static final String MDS_PREFIXES_OMIT = "mds.prefixes.omit";
    }

    /**
     * Holds constants for repository IDs that this provider can serve.
     * @author PaluchM
     *
     */
    public static class RepositoryID{
        public static final String DATACITEMDS = "datacitemds";
    }

    
    /**
     * Holds constants for schema versions.
     * @author PaluchM
     *
     */
    public static class SchemaVersion{
        public static final String VERSION_2_0 = "2.0";
        public static final String VERSION_2_1 = "2.1";
        public static final String VERSION_2_2 = "2.2";
    }
    
    /** Holds constants for database connections **/
    public static class Database{
        
        //MDS DB properties
        public static final String MDS_DB_USERNAME = "mds.db.username";
        public static final String MDS_DB_PASSWORD = "mds.db.password";
        public static final String MDS_DB_URL = "mds.db.url";
        public static final String MDS_DB_DRIVER = "mds.db.driver";
        public static final String MDS_DB_POOL_START = "mds.db.pool.start";
        public static final String MDS_DB_POOL_MAX = "mds.db.pool.max";
        
    }
}
