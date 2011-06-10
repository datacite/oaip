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
 * Interface for a generic data model to model object mapping.
 */
public interface RowMapper {
    /**
     *  This method to implement the ResultSet to model object fields mapping. 
     *  
     * @param rs
     * @param index
     * @return AbstractModel
     * @throws SQLException
     */
    public Object mapRow(ResultSet rs, int index)throws SQLException;       
}

