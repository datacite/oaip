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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;

/**
 * Encapsulates utils for BOM removal. Currently only UTF-8/16/32 supported.
 * @author PaluchM
 *
 */
public class BOMUtil {

    /**UTF-8 Byte Order Mark **/
	public static final byte[] UTF8_BOM = {(byte)0xEF,(byte)0xBB,(byte)0xBF};
	/**UTF-16 Big Endian Byte Order Mark **/
	public static final byte[] UTF16BE_BOM = {(byte)0xFE,(byte)0xFF};
	/**UTF-16 Little Endian Byte Order Mark **/
	public static final byte[] UTF16LE_BOM = {(byte)0xFF,(byte)0xFE};
	/**UTF-32 Big Endian Byte Order Mark **/
	public static final byte[] UTF32BE_BOM = {(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0xFF};
	/**UTF-32 Little Endian Byte Order Mark **/
	public static final byte[] UTF32LE_BOM = {(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00};
		
	/** Mapping of encoding string (alphanumeric only) to BOM **/
	private static final HashMap<String,byte[]> BOM_MAP = new HashMap<String,byte[]>();
	static{
		BOM_MAP.put("UTF8",UTF8_BOM);
		BOM_MAP.put("UTF16LE",UTF16LE_BOM);
		BOM_MAP.put("UTF16BE",UTF16BE_BOM);
		BOM_MAP.put("UTF32BE",UTF32BE_BOM);
		BOM_MAP.put("UTF32LE",UTF32LE_BOM);
	}
	
	/**Hidden constructor**/
	private BOMUtil(){}
	    
    /**
     * Remove a Byte Order Mark from the beginning of a byte[].
     * @param array The byte[] to remove a BOM from.
     * @param encoding The character encoding to remove the BOM for.
     * @return The original byte[] without BOM.
     */
    public static byte[] removeBOM(byte[] array,String encoding) throws UnsupportedEncodingException{
    	if (encoding == null || encoding.trim().length()==0){
    		throw new UnsupportedEncodingException("Unsupported encoding: "+encoding);
    	}
    	else{
    		encoding = encoding.toUpperCase().replaceAll("[^A-Z0-9]","");
    		byte[] bom = BOMUtil.BOM_MAP.get(encoding);
    		if (bom==null){
    			throw new UnsupportedEncodingException("Unsupported encoding: "+encoding);
    		}
    		else{
    			return BOMUtil.removeBOM(array,bom);
    		}  		
    	}
    }
	
	/**
     * Remove a Byte Order Mark from the beginning of a byte[].
     * @param array The byte[] to remove a BOM from.
     * @param BOM The byte[] BOM to remove.
     * @return The original byte[] without BOM.
     */
    public static byte[] removeBOM(byte[] array,byte[] BOM){
    	int i;
    	boolean hasBOM = true;
    	for (i=0;(i<BOM.length)&&hasBOM;i++){
    		if ((byte)array[i] != (byte)BOM[i]){
    			hasBOM = false;
    		}
    	}
    	if (hasBOM){ return ArrayUtils.subarray(array,BOM.length,array.length); }
    	else{ return array; }    	
    }
}
