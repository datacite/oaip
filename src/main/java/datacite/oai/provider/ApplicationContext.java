package datacite.oai.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

import datacite.oai.provider.util.property.PropertyHolder;
import datacite.oai.provider.util.property.PropertyNotFoundException;
import datacite.oai.provider.util.property.PropertyResolver;
import datacite.oai.provider.util.property.PropertyResolverImpl;

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


/**
 * The application context implementation.
 * 
 */
public class ApplicationContext implements PropertyHolder , PropertyResolver {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class);    
    
    //-----------------------------------------------------------------------------------
    // VARIABLE DECLARATIONS
    //-----------------------------------------------------------------------------------

    private Properties applicationProperties;
    private HashMap<String,Properties> repositoryProperties;
        
    private static ApplicationContext instance;
    
    //-----------------------------------------------------------------------------------
    // CONSTRUCTORS and other TRIGGERS
    //-----------------------------------------------------------------------------------

    public static void initialize( ServletContext servletContext ) throws Exception
    {
        instance = new ApplicationContext( servletContext );
    }
    
    public static ApplicationContext getInstance()
    {
        return instance;
    }   

    private ApplicationContext( ServletContext context ) throws Exception
    {
        applicationProperties = loadApplicationProperties(context);
        repositoryProperties = loadRepositoryProperties(applicationProperties);
    }
    
    //-----------------------------------------------------------------------------------
    // PUBLIC METHODS
    //-----------------------------------------------------------------------------------

    public Properties getRepositoryProperties(String repositoryId){
        return this.repositoryProperties.get(repositoryId);
    }
    
    //----------------------------- PropertyHolder methods ------------------------------
    
    /* (non-Javadoc)
     * @see ca.nrc.cisti.common.util.PropertyHolder#getKeys()
     */
    public Set<Object> getKeys() {
        return this.applicationProperties.keySet();
    }

    /* (non-Javadoc)
     * @see ca.nrc.cisti.common.util.PropertyHolder#setProperties(java.util.Properties)
     */
    public void setProperties(Properties properties) {
        Set<Object> keys = properties.keySet();
        for(Object key: keys){
            Object value = properties.get(key);
            this.setProperty((String)key,(String)value);
        }
    }


    /* (non-Javadoc)
     * @see ca.nrc.cisti.common.util.PropertyHolder#setProperty(java.lang.String, java.lang.String)
     */
    public void setProperty(String key, String value) {
        this.applicationProperties.put(key,value);
        getLogger().info("[ApplicationContext] Property "+key+"="+value+" initialized.");
    }
    
    
    //---------------------------- PropertyResolver methods -----------------------------
    
    /* (non-Javadoc)
     * @see ca.nrc.cisti.common.util.PropertyResolver#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String key) throws PropertyNotFoundException, NumberFormatException {
        return new PropertyResolverImpl(this.applicationProperties).getIntProperty(key);
    }

    /* (non-Javadoc)
     * @see ca.nrc.cisti.common.util.PropertyResolver#getProperties(java.lang.String)
     */
    public String[] getProperties(String key) throws PropertyNotFoundException {
        return new PropertyResolverImpl(this.applicationProperties).getProperties(key);
    }

    /* (non-Javadoc)
     * @see ca.nrc.cisti.common.util.PropertyResolver#getProperty(java.lang.String)
     */
    public String getProperty(String key) throws PropertyNotFoundException {
        return new PropertyResolverImpl(this.applicationProperties).getProperty(key);
    }
        
    //-----------------------------------------------------------------------------------
    // PRIVATE METHODS
    //-----------------------------------------------------------------------------------
    protected Logger getLogger(){
        return logger;
    }
    
    @SuppressWarnings("rawtypes")
    private Properties loadApplicationProperties(ServletContext sc) throws Exception{       
        
        Properties props = new Properties();        
        Enumeration paramNames = sc.getInitParameterNames();
        
        int paramCount = 0;
        
        while (paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            String value = sc.getInitParameter(key);
            props.put(key,value);       
            paramCount++;
            logger.info("[ApplicationControllerServlet#init] Init Parameter "+key+"="+value);
        }
        
        //load database properties from file
        try{
            InputStream in = sc.getResourceAsStream(props.getProperty(Constants.Property.DB_PROPERTIES_FILE));
            props.load(in);
            in.close();
        }
        catch(Exception e){
            throw new Exception("Error reading DB properties at: "+props.getProperty(Constants.Property.DB_PROPERTIES_FILE),e);
        }
        
        return props;
        
    }   
    
    @SuppressWarnings("rawtypes")
    private HashMap<String,Properties> loadRepositoryProperties(Properties properties) throws PropertyNotFoundException{        
        HashMap<String,Properties> map = new HashMap<String,Properties>();
        
        String repsValue = (String)properties.get(Constants.Property.REPOSITORIES);  
        
        if (repsValue==null || repsValue.trim().length()==0){
            throw new PropertyNotFoundException("Could not find property: "+Constants.Property.REPOSITORIES);
        }
        
        String[] reps = repsValue.split("[,]");
        
        for (String rep : reps){            
            map.put(rep,new Properties());
        }
        
        for (Entry entry : properties.entrySet()){
            
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            
            String prefix;
            if (key.indexOf(".")<1){
                prefix=null;
            }
            else{
                prefix = key.substring(0,key.indexOf("."));
            }
            
            if (prefix==null || !arrayContains(reps,prefix)){ //non repository specific property
                //applicationProperties.put(key,value);             
            }
            else{
                //repository specific               
                Properties prop = map.get(prefix);
                prop.put(key.replace(prefix+".",""),value);             
                map.put(prefix,prop);               
            }
        }

        return map;
    }   

    private boolean arrayContains(String[] array,String str){
        for (String s : array){
            if (s.equalsIgnoreCase(str)){
                return true;
            }
        }
        return false;
    }   
}
