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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import datacite.oai.provider.util.BOMUtil;
import datacite.oai.provider.util.Log4jPrintWriter;

/**
 * Class used for pooled database access.
 * @author PaluchM
 *
 */
public class PooledDataSource {
	
	private static Logger logger = Logger.getLogger(PooledDataSource.class);   
    private PoolingDataSource pds;

    /*************************************/
    /** CONSTRUCTORS / SETUP / TEARDOWN **/
    /*************************************/
    
    /**
     * Public constructor
     * @throws Exception
     */
    public PooledDataSource(String driver,String url,String username,String password,int startPoolSize,int maxPoolSize) throws SQLException {                    
                        
            try{
                logger.warn("Initializing pooled data source for: "+url);
                Class.forName(driver, true, DriverManagerConnectionFactory.class.getClassLoader()).newInstance();
                                
                ObjectPool connectionPool = new CustomConnPool(startPoolSize,maxPoolSize);
                ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url,username,password);
                
                PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
                poolableConnectionFactory.setValidationQuery("select 1+1 from dual");
                
                pds = new PoolingDataSource(connectionPool);
                pds.setLogWriter(new Log4jPrintWriter(logger,Priority.WARN));
                
                logger.warn("Done.");
            }
            catch(Exception e){
                throw new SQLException(e);
            }
    }
    
    public void close(){
        pds = null;
    }

    /***************************/
    /** PUBLIC STATIC METHODS **/
    /***************************/

    /**
     * Conversion method to convert a java.sql.Blob object to a string. 
     * @param blb The Blob object to convert.
     * @param enc The encoding on the Blob object.
     * @return A string result or an empty string if the Blob is null.
     * @throws Exception
     */
    public static String blobToString(java.sql.Blob blb,String enc) throws Exception{
        byte[] bytes = null;
        
        if (blb==null){return "";}
                
        if (blb.length() > Integer.MAX_VALUE){
            throw new Exception("BLOB size too large. Length value > Integer.MAX_VALUE.");
        }                   

        bytes = blb.getBytes((long)1,(int)blb.length());
        bytes = BOMUtil.removeBOM(bytes,enc);
        
        return new String(bytes,enc);                   
    }
    
    
    /********************************/
    /** PUBLIC METHODS             **/
    /********************************/
    
    /**
     * Method to get a connection from the pool.
     * @return A connection object from the pool.
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException{
        return pds.getConnection();
    }
    
    
    /**
     * Runs a query designed to return a single object.
     * @param query The query to execute as a string.
     * @param params The list of query parameters in order. 
     * @param rm The RowMapper implementation for the desired object.
     * @return The desired object after being constructed by the RowMapper implementation or null if the query generated no results.
     * @throws SQLException
     */
    public Object runQuery(String query,String[] params,RowMapper rm) throws SQLException{
        return runQuery(query,params,1,rm);
    }


    /**
     * Runs a query designed to return a single object.
     * @param query The query to execute as a string. 
     * @param rm The RowMapper implementation for the desired object.
     * @return The desired object after being constructed by the RowMapper implementation or null if the query generated no results.
     * @throws SQLException
     */
    public Object runQuery(String query,RowMapper rm) throws SQLException{
        return runQuery(query,null,1,rm);
    }
    
    /**
     * Runs a query designed to return a single string result.
     * @param query The query to execute as a string. 
     * @param columnIndex The column to return a string (indexing starts at 1).
     * @return The column value as a string.
     * @throws SQLException
     */
    public String runQueryForString(String query,int columnIndex) throws SQLException{
        return (String)runQuery(query,null,columnIndex,new StringRowMapper());
    }    

    /**
     * Runs a query designed to return a list of objects.
     * @param query The query to execute as a string.
     * @param params The list of query parameters in order. 
     * @param rm The RowMapper implementation for the desired object.
     * @return The list of desired objects after being constructed by the RowMapper implementation.
     * @throws SQLException
     */
    public Object[] runQueryForList(String query,RowMapper rm) throws SQLException{
        return runQueryForList(query,null,1,rm);
    }

    /**
     * Runs a query designed to return a list of objects.
     * @param query The query to execute as a string.
     * @param params The list of query parameters in order. 
     * @param columnIndex The column index to return (indexing starts at 1).
     * @param rm The RowMapper implementation for the desired object.
     * @return The list of desired objects after being constructed by the RowMapper implementation.
     * @throws SQLException
     */
    public Object[] runQueryForList(String query,String[] params,int columnIndex, RowMapper rm) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LinkedList<Object> results = new LinkedList<Object>();
        
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
        
            if (params!=null && params.length>0){
                int index = 1;
                for (String str : params){
                    pstmt.setObject(index,str);
                    index++;
                }
            }
        
            rs = pstmt.executeQuery(); 
            
            while(rs.next()){
                results.add(rm.mapRow(rs,columnIndex));
            }
            
            return results.toArray(new Object[results.size()]);
        }
        catch(SQLException e){
            throw e;
        }
        catch(Exception e){
            throw new SQLException(e);
        }
        finally{
            if (rs!=null){rs.close();}
            if (pstmt!=null){pstmt.close();}
            if (conn!=null){conn.close();}
        }
    }    
      

    /******************************************************/
    /** PRIVATE METHODS                                  **/
    /******************************************************/
    
    /**
     * Runs a query designed to return a single object.
     * @param query The query to execute as a string.
     * @param params The list of query parameters in order. 
     * @param columnIndex The columnIndex to read (indexing starts at 1).
     * @param rm The RowMapper implementation for the desired object.
     * @return The desired object after being constructed by the RowMapper implementation or null if the query generated no results.
     * @throws SQLException
     */
    private Object runQuery(String query,String[] params,int columnIndex,RowMapper rm) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
        
            if (params!=null && params.length>0){
                int index = 1;
                for (String str : params){
                    pstmt.setObject(index,str);
                    index++;
                }
            }
        
            rs = pstmt.executeQuery(); 
            
            if (rs.next()){            
                return rm.mapRow(rs,columnIndex);
            }
            else{
                return null;
            }
        }
        catch(SQLException e){
            throw e;
        }
        catch(Exception e){
            throw new SQLException(e);
        }
        finally{
            if (rs!=null){rs.close();}
            if (pstmt!=null){pstmt.close();}
            if (conn!=null){conn.close();}
        }
    }    
    
}
