package datacite.oai.provider.util;

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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.oclc.oai.server.verb.BadResumptionTokenException;

public class ResumptionToken {

    private static final Logger logger = Logger.getLogger(ResumptionToken.class);   
    
    private final String    delimiter = ",";
    private final int       tokenCount = 6; //we store 6 elements in the token
    
    private String  resumeId;
    private String  from;
    private String  until;
    private String  set;
    private String  prefix;
    
    private int     count;   
    
    public ResumptionToken(){
        resumeId = "";
        from = "";
        until = "";
        set = "";
        prefix = "";
        count = -1; 
    }
    
    public ResumptionToken(String token) throws BadResumptionTokenException{
        this();
        this.parseToken(token);         
    }   

    //*************************
    // Public methods
    //*************************
    
    public String toString(){       
        return resumeId+delimiter+from+delimiter+until+delimiter+count+delimiter+set+delimiter+prefix;      
    }
        
    public String getId(){
        return resumeId;
    }

    public void setId(String resumeId){
        this.resumeId = resumeId;
    }

    public String getFromDate(){
        return from;
    }
    
    public void setFromDate(String from){
        this.from = from;
    }

    public String getUntilDate(){
        return until;
    }
    
    public void setUntilDate(String until){
        this.until = until;
    }

    public int getRecordCount(){
        return count;
    }

    public void setRecordCount(int count){
        this.count = count;
    }

    public String getSet(){
        return set;
    }
    
    public void setSet(String set){
        this.set = set;
    }

    public String getPrefix(){
        return prefix;
    }
    
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }


    //***************************
    // Private Methods
    //***************************

    private void parseToken(String token) throws BadResumptionTokenException{

        try {       
            StringTokenizer tokenizer = new StringTokenizer(token,this.delimiter);              
                    
            if (tokenizer.countTokens()!=this.tokenCount){
                logger.error("Found "+tokenizer.countTokens()+" tokens. Expecting "+this.tokenCount+" token="+token);
                throw new BadResumptionTokenException();
            }
        
            resumeId = tokenizer.nextToken();
            from = tokenizer.nextToken();
            until = tokenizer.nextToken();            
            count = Integer.parseInt(tokenizer.nextToken());
            set = tokenizer.nextToken();            
            prefix = tokenizer.nextToken();
        } 
        catch (NoSuchElementException e) {
            logger.error("Element missing from token."+e.getMessage()+" token="+token);            
            throw new BadResumptionTokenException();
        }
        catch (Exception e){
            logger.error("Exception! "+e.getMessage()+ " token="+token);
            throw new BadResumptionTokenException();            
        }
    }
    
}
