package datacite.oai.provider.service;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import datacite.oai.provider.ApplicationContext;
import datacite.oai.provider.Constants;
import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.catalog.datacite.SetRecordBean;
import datacite.oai.provider.util.Pair;

public class MDSSearchServiceSolrImpl extends MDSSearchService {

    private static final Logger logger = Logger.getLogger(MDSSearchServiceSolrImpl.class);

    private String url = "http://localhost:8080/search";

    private MDSSearchServiceSqlImpl sqlService;

    private CommonsHttpSolrServer solrServer;

    public MDSSearchServiceSolrImpl(ServletContext servletContext) throws ServiceException {
        super(servletContext);
        try {
            ApplicationContext context = ApplicationContext.getInstance(); 
            
            String username = "foo";
            String password = "bar";
            
            sqlService = new MDSSearchServiceSqlImpl(servletContext);
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
            return convertSolrDocumentToRecord(doc);
        } catch (SolrServerException e) {
            throw new ServiceException(e);
        }
    }

    private DatasetRecordBean convertSolrDocumentToRecord(SolrDocument doc) {
        String id = (String) doc.getFieldValue("dataset_id");
        String symbol = (String) doc.getFieldValue("datacentre_symbol");
        String metadata = new String((byte[]) doc.getFieldValue("xml"));
        Date updateDate = (Date) doc.getFieldValue("uploaded");
        Boolean refQuality = (Boolean) doc.getFieldValue("refQuality");
        Boolean isActive = (Boolean) doc.getFieldValue("has_metadata");
        String schemaVersion = (String) doc.getFieldValue("schema_version");

        DatasetRecordBean record = new DatasetRecordBean(id, metadata, updateDate, refQuality, isActive, symbol);
        record.setSchemaVersion(schemaVersion);
        return record;
    }

    @Override
    public Pair<List<DatasetRecordBean>, Integer> getDatasets(Date updateDateFrom, Date updateDateTo, String setspec,
            int offset, int length) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pair<List<SetRecordBean>, Integer> getSets() throws ServiceException {
        return sqlService.getSets();
    }

    @Override
    public Pair<List<SetRecordBean>, Integer> getSets(int offset, int length) throws ServiceException {
        return sqlService.getSets(offset, length);
    }

    @Override
    public void destroy() {
        sqlService.destroy();
    }

}
