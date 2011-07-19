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
import java.util.Date;

import datacite.oai.provider.Constants;
import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.util.ThreadSafeSimpleDateFormat;

/**
 * RowMapper implementation for a DatasetRecordBean object.
 * @author PaluchM
 */
public class DatasetRowMapper implements RowMapper{
	
    public Object mapRow(ResultSet rs) throws SQLException{
        return mapRow(rs,1);
    }
    
    public Object mapRow(ResultSet rs, int index) throws SQLException {
        ThreadSafeSimpleDateFormat df = new ThreadSafeSimpleDateFormat(Constants.DateTime.DATETIME_FORMAT);
        String id = null;
        String symbol = null;
        String metadata = null;
        Date updateDate = null;
        
        boolean refQuality = false;
        boolean isActive = false;
        
        try{
            id = rs.getString("dataset_id");
            symbol = rs.getString("symbol");                                                                        
            metadata = PooledDataSource.blobToString(rs.getBlob("xml"),"UTF8");                            
            isActive = rs.getBoolean("is_active");
            updateDate = df.parse(rs.getString("update_date"));
            refQuality = rs.getBoolean("is_ref_quality");
                
            return new DatasetRecordBean(id,metadata,updateDate,refQuality,isActive,symbol);
        }
        catch(SQLException se){
            throw se;
        }
        catch(Exception e){
            throw (SQLException)new SQLException(e);
        }
    }        
}