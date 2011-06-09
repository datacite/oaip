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

/**
 * A RowMapper implementation for a string. 
 * @author PaluchM
 *
 */
public class StringRowMapper implements RowMapper{

    @Override
    /**
     * Returns the 'columnIndex' column as a string. (Column indexing begins at 1).
     */
    public Object mapRow(ResultSet rs, int columnIndex) throws SQLException {
        try{            
            return rs.getString(columnIndex);            
        }
        catch(SQLException sqle){
            throw sqle;
        }
        catch(Exception e){
            throw new SQLException(e);
        }
    }
}
