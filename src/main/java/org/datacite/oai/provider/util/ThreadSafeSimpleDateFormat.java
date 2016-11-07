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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * A thread safe version of a couple methods from the SimpleDateFormat class.
 * @author PaluchM
 *
 */
public class ThreadSafeSimpleDateFormat {

    private String dateTimeFormat;
    private Locale locale;
    private SimpleTimeZone timeZone;
        
    /**
     * A thread safe version of a couple methods from the SimpleDateFormat class.
     * @param dateTimeFormat
     * @param locale
     * @param timeZone
     */
    public ThreadSafeSimpleDateFormat(String dateTimeFormat,Locale locale,SimpleTimeZone timeZone){
        this.dateTimeFormat = dateTimeFormat;
        this.locale = locale;
        this.timeZone = timeZone;
    }
    
    /**
     * A thread safe version of a couple methods from the SimpleDateFormat class. 
     * Sets the time zone to UTC by default.
     * @param dateTimeFormat
     * @param locale 
     */
    public ThreadSafeSimpleDateFormat(String dateTimeFormat,Locale locale){
        this.dateTimeFormat = dateTimeFormat;
        this.locale = locale;
        this.timeZone = new SimpleTimeZone(0,"UTC");
    }

    /**
     * A thread safe version of a couple methods from the SimpleDateFormat class. 
     * Sets the time zone to UTC and Locale to US by default.
     * @param dateTimeFormat
     */
    public ThreadSafeSimpleDateFormat(String dateTimeFormat){
        this.dateTimeFormat = dateTimeFormat;
        this.locale = Locale.US;        
        this.timeZone = new SimpleTimeZone(0,"UTC");        
    }
    
    public String format(Date date){
        return getSimpleDateFormat().format(date);
    }

    public Date parse(String dateTimeString) throws ParseException{
        return getSimpleDateFormat().parse(dateTimeString);
    }

    private SimpleDateFormat getSimpleDateFormat(){
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat,locale);
        sdf.setTimeZone(timeZone);
        return sdf;
    }   
}
