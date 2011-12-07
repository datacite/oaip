package datacite.oai.provider.service;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import datacite.oai.provider.catalog.datacite.DatasetRecordBean;
import datacite.oai.provider.catalog.datacite.SetRecordBean;
import datacite.oai.provider.util.Pair;

public class MDSSearchServiceSolrImpl extends MDSSearchService {
    
    MDSSearchServiceSqlImpl sqlService;

    public MDSSearchServiceSolrImpl(ServletContext servletContext) throws ServiceException {
        super(servletContext);
        sqlService = new MDSSearchServiceSqlImpl(servletContext);
    }

    @Override
    public DatasetRecordBean getDatasetByID(String id) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
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
