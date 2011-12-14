package datacite.oai.provider.service;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import datacite.oai.provider.ApplicationContext;
import datacite.oai.provider.Constants;
import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.catalog.datacite.SetRecordBean;
import datacite.oai.provider.util.BOMUtil;
import datacite.oai.provider.util.Pair;
import datacite.oai.provider.util.ThreadSafeSimpleDateFormat;

public class MDSSearchServiceSolrImpl extends MDSSearchService {

    private static final Logger logger = Logger.getLogger(MDSSearchServiceSolrImpl.class);

    private CommonsHttpSolrServer solrServer;

    ThreadSafeSimpleDateFormat dateFormat = new ThreadSafeSimpleDateFormat(Constants.DateTime.DATETIME_FORMAT_SOLR);

    public MDSSearchServiceSolrImpl(ServletContext servletContext) throws ServiceException {
        super(servletContext);
        try {
            ApplicationContext context = ApplicationContext.getInstance();

            String url = context.getProperty(Constants.Database.MDS_SOLR_URL);
            String username = context.getProperty(Constants.Database.MDS_SOLR_USERNAME);
            String password = context.getProperty(Constants.Database.MDS_SOLR_PASSWORD);

            solrServer = new CommonsHttpSolrServer(url);
            setSolrCredentials(username, password);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private void setSolrCredentials(String username, String password) throws MalformedURLException {
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, null, null);
        Credentials credentials = new UsernamePasswordCredentials(username, password);
        solrServer.getHttpClient().getState().setCredentials(scope, credentials);
    }

    @Override
    public DatasetRecordBean getDatasetByID(String id) throws ServiceException {
        SolrQuery query = new SolrQuery();
        query.setQuery("dataset_id:" + id);

        try {
            QueryResponse response = solrServer.query(query);
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
        String metadata = new String(BOMUtil.removeBOM(xml, "UTF-8"), "UTF-8");
        Date updateDate = (Date) doc.getFieldValue("uploaded");
        Boolean refQuality = (Boolean) doc.getFieldValue("refQuality");
        Boolean isActive = (Boolean) doc.getFieldValue("has_metadata");
        String schemaVersion = (String) doc.getFieldValue("schema_version");

        DatasetRecordBean record = new DatasetRecordBean(id, metadata, schemaVersion, updateDate, refQuality, isActive, symbol);
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
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(length);
        query.setStart(offset);
        query.setSortField("uploaded", ORDER.asc);

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

        query.addFilterQuery("uploaded:[" + from + " TO " + to + "]");

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
    public void destroy() {
    }

}
