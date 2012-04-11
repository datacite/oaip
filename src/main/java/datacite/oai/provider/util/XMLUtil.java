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

import java.io.UnsupportedEncodingException;

/**
 * Util class to convert and clean byte[] of XML
 * @author PaluchM
 *
 */
public class XMLUtil {

	
	/** Hide */
	private XMLUtil(){}
	

	/**
	 * Converts byte[] of XML into a string, removing comment blocks and XML declaration and BOM. Defaults to UTF8 encoding.
	 * @param xml The XML as a byte[]
	 * @return The XML as a String, cleaned and without BOM.
	 * @throws UnsupportedEncodingException
	 */
	public static String toXMLString(byte[] xml) throws UnsupportedEncodingException{
		return toXMLString(xml,"UTF-8");
	}
	
	/**
	 * Converts byte[] of XML into a string, removing comment blocks and XML declaration and BOM.
	 * @param xml The XML as a byte[]
	 * @param enc The encoding to use when converting (UTF-8,UTF-16LE/BE, UTF-32LE/BE supported).
	 * @return The XML as a String, cleaned and without BOM.
	 * @throws UnsupportedEncodingException
	 */
	public static String toXMLString(byte[] xml,String enc) throws UnsupportedEncodingException{
		String result;
		
		xml = BOMUtil.removeBOM(xml, enc);
		result = new String(xml,enc);
		return cleanXML(result);
	}
	
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
