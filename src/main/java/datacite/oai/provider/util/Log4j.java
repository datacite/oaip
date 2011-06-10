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

import java.net.UnknownHostException;
import java.net.InetAddress;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SMTPAppender;

import datacite.oai.provider.ApplicationContext;
import datacite.oai.provider.Constants;


public final class Log4j
{
    /*********************************************************************************************
     * VARIABLE DECLARATIONS                                                                     *
     *********************************************************************************************/

    // Defined in log4j.properties
    private static enum Appender
    {
        LogFile,
        SevereMail
    }

    /*********************************************************************************************
     * CONSTRUCTORS AND PUBLIC INITIALISATION                                                    *
     *********************************************************************************************/

    /*********************************************************************************************
     * PUBLIC METHODS                                                                            *
     *********************************************************************************************/

    /**
     * Sets the log level and configures the appenders for Log4j
     */
    public static void initialize() throws Exception
    {
        ApplicationContext context = ApplicationContext.getInstance();

        // Set the Log4j Root Logger Level
        // If logLevel is unintelligible, level is set to DEBUG.
        Level logLevel = Level.toLevel(context.getProperty(Constants.Property.LOG4J_ROOT_LEVEL));
        Logger.getRootLogger().setLevel(logLevel);

        // Get the SevereMail appender
/*        SMTPAppender appender = (SMTPAppender)Logger.getRootLogger().getAppender(Appender.SevereMail.name());

        // Generate the subject to appear in the email
        StringBuilder emailSubject = new StringBuilder();
        emailSubject.append("[ ");
        emailSubject.append(context.getProperty(Constants.Property.ENVIRONMENT_LABEL));
        emailSubject.append(" - ");

        try
        {
            emailSubject.append(InetAddress.getLocalHost().getHostName());
        }
        catch(UnknownHostException e)
        {
            emailSubject.append("UNKNOWN HOST");
        }

        emailSubject.append(" ] ");
        emailSubject.append(appender.getSubject());

        // Set the subject
        appender.setSubject(emailSubject.toString());

        // Set the recipient list
        appender.setTo(context.getProperty(Constants.Property.LOG4J_SEVEREMAIL_TO));

        // Set the SMTP host
        appender.setSMTPHost(context.getProperty(Constants.Property.SMTP_HOST));

        appender.activateOptions();
*/        
    }

    /*********************************************************************************************
     * PRIVATE METHODS                                                                           *
     *********************************************************************************************/

}
