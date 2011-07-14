package datacite.oai.provider.service;

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


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import datacite.oai.provider.ApplicationContext;
import datacite.oai.provider.Constants;

/**
 * A service implementation that performs local XSL transformations.
 */
public class TransformerService extends Service {

    private static Logger logger = Logger.getLogger(TransformerService.class);
    private Templates kernel2_0ToOaidcTemplates;
    private Templates kernel2_1ToOaidcTemplates;
    private Templates kernel2_2ToOaidcTemplates;

    /**
     * Public constructor
     * @param context
     * @throws ServiceException
     */
    public TransformerService(ServletContext context) throws ServiceException {

        super(context);

        try {
            logger.warn("TransformerService loading...");
            ApplicationContext applicationContext = ApplicationContext.getInstance();
                        
            logger.warn("Loading Kernel2.0 transform");
            String resourcePath = applicationContext.getProperty(Constants.Property.STYLESHEET_KERNEL2_0_TO_OAIDC);            
            DOMSource domSource = buildDOMSource(context.getResourceAsStream(resourcePath));            
            kernel2_0ToOaidcTemplates = TransformerFactory.newInstance().newTemplates(domSource);

            logger.warn("Loading Kernel2.1 transform");
            resourcePath = applicationContext.getProperty(Constants.Property.STYLESHEET_KERNEL2_1_TO_OAIDC);
            domSource = buildDOMSource(context.getResourceAsStream(resourcePath));            
            kernel2_1ToOaidcTemplates = TransformerFactory.newInstance().newTemplates(domSource);
            
            logger.warn("Loading Kernel2.2 transform");
            resourcePath = applicationContext.getProperty(Constants.Property.STYLESHEET_KERNEL2_2_TO_OAIDC);
            domSource = buildDOMSource(context.getResourceAsStream(resourcePath));            
            kernel2_2ToOaidcTemplates = TransformerFactory.newInstance().newTemplates(domSource);
            
            logger.warn("TransformerService loaded.");

        } 
        catch(TransformerConfigurationException te){
        	throw new ServiceException(te.getMessageAndLocation(),te);
        }
        catch(Exception e) {
            logger.error("Could not load TransformerService", e);
            throw new ServiceException(e);
        }
    }

    
    @Override
    public void destroy() {
    }

    /**
     * Transform DataCite Metadata Scheme 2.0 to OAI Dublin Core.
     * @param metadata The metadata to transform
     * @return The resulting metadata as a String
     * @throws ServiceException
     */
    public String doTransform_Kernel2_0ToOaidc(String metadata) throws ServiceException{
        return doTransform_kernelToOaidc(metadata,this.kernel2_0ToOaidcTemplates,Constants.SchemaVersion.VERSION_2_0);
    }
    
    /**
     * Transform DataCite Metadata Scheme 2.1 to OAI Dublin Core.
     * @param metadata The metadata to transform
     * @return The resulting metadata as a String
     * @throws ServiceException
     */
    public String doTransform_Kernel2_1ToOaidc(String metadata) throws ServiceException{
        return doTransform_kernelToOaidc(metadata,this.kernel2_1ToOaidcTemplates,Constants.SchemaVersion.VERSION_2_1);
    }    
    
    /**
     * Transform DataCite Metadata Scheme 2.1 to OAI Dublin Core.
     * @param metadata The metadata to transform
     * @return The resulting metadata as a String
     * @throws ServiceException
     */
    public String doTransform_Kernel2_2ToOaidc(String metadata) throws ServiceException{
        return doTransform_kernelToOaidc(metadata,this.kernel2_2ToOaidcTemplates,Constants.SchemaVersion.VERSION_2_2);
    }
    
    /**
     * Transform DataCite Metadata Scheme to OAI Dublin Core.
     * @param metadata the metadata to transform
     * @param template the template to use
     * @param version the version number
     * @return
     * @throws ServiceException
     */
    private String doTransform_kernelToOaidc(String metadata,Templates template,String version) throws ServiceException{

        // Configure input
        StringReader stringReader = new StringReader(metadata);
        StreamSource streamSource = new StreamSource(stringReader);

        // Configure output
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);

        // Do transform
        try {
            Transformer transformer = template.newTransformer();
            transformer.transform(streamSource, streamResult);

            // Return result
            stringWriter.close();
            return stringWriter.toString();

        } catch (Exception e) {
            logger.error( "Unable to transform Kernel "+version+" to OAIDC: " + metadata, e );
            throw new ServiceException("Unable to transform Kernel "+version+" to OAIDC", e );
        }
    }    
    
    /**
     * Builds a DOMSource object from the input steam
     * @param inputStream
     * @return The DOMSource object
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private DOMSource buildDOMSource(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        documentFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);

        return new DOMSource(document);
        
    }    
}
