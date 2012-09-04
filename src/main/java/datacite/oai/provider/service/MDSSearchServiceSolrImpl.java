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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;

import datacite.oai.provider.ApplicationContext;
import datacite.oai.provider.Constants;
import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.catalog.datacite.SetRecordBean;
import datacite.oai.provider.util.Pair;
import datacite.oai.provider.util.ThreadSafeSimpleDateFormat;

public class MDSSearchServiceSolrImpl extends MDSSearchService {

    private static final Logger logger = Logger.getLogger(MDSSearchServiceSolrImpl.class);

    private CommonsHttpSolrServer solrServer;

    static ThreadSafeSimpleDateFormat dateFormat = new ThreadSafeSimpleDateFormat(Constants.DateTime.DATETIME_FORMAT_SOLR);

    public MDSSearchServiceSolrImpl(ServletContext servletContext) throws ServiceException {
        super(servletContext);
        try {
            ApplicationContext context = ApplicationContext.getInstance();
            
            String url = context.getProperty(Constants.Database.MDS_SOLR_URL);
            solrServer = new CommonsHttpSolrServer(url);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }    

    @Override
    public DatasetRecordBean getDatasetByID(String id) throws ServiceException {
        SolrQuery query = new SolrQuery();
        query.setQuery("dataset_id:" + id);
        query.addFilterQuery("has_metadata:true");

        try {
            QueryResponse response = solrServer.query(query);
            if (response.getResults().isEmpty())
                return null;
            SolrDocument doc = response.getResults().get(0);
            return convertToRecord(doc);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private DatasetRecordBean convertToRecord(SolrDocument doc) throws UnsupportedEncodingException {
        String id = (String) doc.getFieldValue("dataset_id");
        String symbol = (String) doc.getFieldValue("datacentre_symbol");
        byte[] xml = (byte[]) doc.getFieldValue("xml");
        Date updateDate = (Date) doc.getFieldValue("updated");
        Boolean refQuality = (Boolean) doc.getFieldValue("refQuality");
        Boolean isActive = (Boolean) doc.getFieldValue("has_metadata");
        String schemaVersion = (String) doc.getFieldValue("schema_version");

        DatasetRecordBean record = new DatasetRecordBean(id, xml, schemaVersion, updateDate, refQuality, isActive, symbol);
        return record;
    }

    private List<DatasetRecordBean> convertToRecords(SolrDocumentList docs) throws UnsupportedEncodingException {
        List<DatasetRecordBean> list = new ArrayList<DatasetRecordBean>();
        for (SolrDocument doc : docs)
            list.add(convertToRecord(doc));
        return list;
    }

    @Override
    public Pair<List<DatasetRecordBean>, Integer> getDatasets(Date updateDateFrom, Date updateDateTo, String setspec,
            int offset, int length) throws ServiceException {
        
        SolrQuery query = constructSolrQuery(updateDateFrom, updateDateTo, setspec, offset, length);

        logger.info(query);

        try {
            QueryResponse response = solrServer.query(query);
            SolrDocumentList results = response.getResults();

            List<DatasetRecordBean> records = convertToRecords(results);
            int count = (int) results.getNumFound();
            return new Pair<List<DatasetRecordBean>, Integer>(records, count);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    static SolrQuery constructSolrQuery(Date updateDateFrom, Date updateDateTo, String setspec, int offset, int length) throws ServiceException {
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(length);
        query.setStart(offset);
        query.setSortField("updated", ORDER.asc);
        query.addFilterQuery("has_metadata:true");

        setspec = StringUtils.trimToEmpty(setspec);
        if (setspec.contains(Constants.Set.BASE64_PART_DELIMITER)) {
            String split[] = setspec.split(Constants.Set.BASE64_PART_DELIMITER, 2);
            setspec = split[0];
            String base64 = split[1];
            String solrfilter = new String(Base64.decodeBase64(base64));
            logger.info("decoded base64 setspec: " + solrfilter);
            solrfilter = solrfilter.replaceAll("^[?&]+", "");
            
            List<NameValuePair> params = URLEncodedUtils.parse(solrfilter, Charset.defaultCharset());
            for (NameValuePair param : params) {
                String name = param.getName();
                String value = param.getValue();
                if (name.equals("q")) 
                    query.setQuery(value);
                else if (name.equals("fq"))
                    query.addFilterQuery(value);
                else
                    throw new ServiceException("parameter '" + name + "' is not supported");
            }
        }
        
        
        if (setspec != null && setspec.trim().length() > 0) {
            setspec = setspec.trim().toUpperCase();

            if (setspec.equalsIgnoreCase(Constants.Set.REF_QUALITY)) {
                query.addFilterQuery("refQuality:true");
            } else {
                if (setspec.endsWith(Constants.Set.REF_QUALITY_SUFFIX)) {
                    query.addFilterQuery("refQuality:true");
                    setspec = setspec.substring(0, setspec.lastIndexOf(Constants.Set.REF_QUALITY_SUFFIX));
                }
                String field = setspec.contains(".") ? "datacentre_symbol" : "allocator_symbol";
                query.addFilterQuery(field + ":" + setspec);
            }
        }

        String from = dateFormat.format(updateDateFrom);
        String to = dateFormat.format(updateDateTo);

        query.addFilterQuery("updated:[" + from + " TO " + to + "]");
        
        query.setParam(CommonParams.QT, "/api");

        return query;
    }

    @Override
    public Pair<List<SetRecordBean>, Integer> getSets() throws ServiceException {
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(0);
        query.setFacet(true);
        query.setFacetLimit(-1);
        query.addFacetField("allocator_facet", "datacentre_facet");

        try {
            QueryResponse response = solrServer.query(query);

            SortedSet<String> facetValues = new TreeSet<String>();
            for (FacetField facet : response.getFacetFields()) {
                for (Count count : facet.getValues()) {
                    facetValues.add(count.getName());
                }
            }

            ArrayList<SetRecordBean> sets = new ArrayList<SetRecordBean>();
            for (String facetValue : facetValues) {
                String[] parts = facetValue.split(" - ", 2);
                String symbol = parts[0];
                String name = parts[1];
                sets.add(new SetRecordBean(symbol, name));
            }

            return new Pair<List<SetRecordBean>, Integer>(sets, sets.size());

        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }

	@Override
	public boolean getStatus() throws ServiceException {
		logger.info("MDS Search Service status check...");
		
		try{
			SolrPingResponse response = solrServer.ping();
			if (response != null && response.getStatus() == 0){
				logger.info("Service status: OK");
				logger.info("[Elapsed time: "+response.getElapsedTime()+" Status code: "+response.getStatus()+"]");
				return true;				
			}
			else{
				logger.warn("Service status: MDS search service unavailable.");
				if (response != null){
					logger.warn("[Elapsed time: "+response.getElapsedTime()+" Status code: "+response.getStatus()+"]");
				}
				return false;
			}
		}
		catch(Exception e){
			throw new ServiceException(e);
		}
	}

	
    @Override
    public void destroy() {
    }



}
