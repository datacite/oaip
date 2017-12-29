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

import org.apache.commons.lang.StringEscapeUtils;

import datacite.oai.provider.Constants;

/**
 * Encapsulates the properties and values of a set.
 * @author PaluchM
 *
 */
public class SetRecordBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private String name;
    private String symbol;

    /**
     * Public constructor.
     * @param symbol
     * @param name
     */
    public SetRecordBean(String symbol, String name){
        this.symbol = symbol;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getSymbol(){
        return symbol;
    }

    /**
     * Returns the "setSpec" value for this set.
     */
    public String getSetSpec(){
        StringBuilder sb = new StringBuilder();

        sb.append("<set><setSpec>");
        sb.append(StringEscapeUtils.escapeXml(symbol));
        sb.append("</setSpec><setName>");
        sb.append(StringEscapeUtils.escapeXml(name));
        sb.append("</setName></set>");
        return sb.toString();
    }
}
