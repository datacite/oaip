package datacite.oai.provider.util;

/*******************************************************************************
* Copyright (c) 2012 DataCite
*
* All rights reserved. This program and the accompanying 
* materials are made available under the terms of the 
* Apache License, Version 2.0 which accompanies 
* this distribution, and is available at 
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/


/**
 * Util class to convert and clean XML
 * @author PaluchM
 *
 */
public class XMLUtil {

	
	/** Hide */
	private XMLUtil(){}

	
	/**
	 * Removes all comment blocks and XML declaration.
	 * @param xml The XML to clean.
	 * @return The XML as a string minus comment clocks and XML declaration
	 */
	public static String cleanXML(String xml){
		if (xml == null){
			return xml;
		}
		else{
			return xml.replaceAll("(<!--.*-->)","").replaceAll("(<\\?xml.*\\?>)","");
		}				
	}
	
}
