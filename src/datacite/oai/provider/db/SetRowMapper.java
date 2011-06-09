package datacite.oai.provider.db;

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

import java.sql.ResultSet;
import java.sql.SQLException;

import datacite.oai.provider.catalog.datacite.SetRecordBean;

/**
 * Row Mapper implementation for the SetRecordBean object.
 * @author PaluchM
 *
 */
public class SetRowMapper implements RowMapper{

    public Object mapRow(ResultSet rs) throws SQLException {
        return mapRow(rs,1);
    }
    
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        String symbol = null;
        String name = null;

        try{
            symbol = rs.getString("symbol");
            name = rs.getString("name");                                                                        
                
            return new SetRecordBean(symbol,name);

        }
        catch(SQLException se){
            throw se;
        }
        catch(Exception e){
            throw (SQLException)new SQLException(e);
        }
    }        
}
