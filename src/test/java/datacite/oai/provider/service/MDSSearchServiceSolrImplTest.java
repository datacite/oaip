package datacite.oai.provider.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import datacite.oai.provider.Constants;

public class MDSSearchServiceSolrImplTest {

    String fromStr = "2001-02-13T08:09:11Z";
    String toStr = "2012-05-13T18:04:26Z";

    Date from;
    Date to;

    @Before
    public void init() throws Exception {
        from = DateUtil.parseDate(fromStr);
        to = DateUtil.parseDate(toStr);
    }

    @Test
    public void testQuery() throws Exception {
        String set = null;
        Integer offset = 23;
        Integer length = 42;

        SolrQuery query = MDSSearchServiceSolrImpl.constructSolrQuery(from, to, set, offset, length);
        String[] filtersActual = query.getFilterQueries();
        String[] filtersExpected = { "has_metadata:true", "uploaded:[" + fromStr + " TO " + toStr + "]" };

        assertEquals("*:*", query.getQuery());
        assertEquals(length, query.getRows());
        assertEquals(offset, query.getStart());
        assertArrayEquals(filtersExpected, filtersActual);
    }
    
    @Test
    public void testQuerySet() {
        testSet(Constants.Set.REF_QUALITY, "refQuality:true");
        testSet("foo", "allocator_symbol:FOO");
        testSet("foo" + Constants.Set.REF_QUALITY_SUFFIX, "allocator_symbol:FOO", "refQuality:true");
        testSet("foo.DC", "datacentre_symbol:FOO.DC");
        testSet("foo.DC" + Constants.Set.REF_QUALITY_SUFFIX, "datacentre_symbol:FOO.DC", "refQuality:true");
        
        testSet("");
        testSet("   ");
        testSet("  foo", "allocator_symbol:FOO");
        testSet("foo  ", "allocator_symbol:FOO");

    }
    
    
    private SolrQuery testSet(String set, String... filtersExpected) {
        SolrQuery query = MDSSearchServiceSolrImpl.constructSolrQuery(from, to, set, 0, 50);
        String[] filtersActual = query.getFilterQueries();
        
        String[] filterAdditional = { "has_metadata:true", "uploaded:[" + fromStr + " TO " + toStr + "]" };
        filtersExpected = (String[]) ArrayUtils.addAll(filtersExpected, filterAdditional);

        Arrays.sort(filtersActual);
        Arrays.sort(filtersExpected);
        
        assertArrayEquals(filtersExpected, filtersActual);
        return query;
    }
    
    @Test
    public void testQuerySetBase64() {
        testSet(Constants.Set.BASE64_PART_DELIMITER);
        
        SolrQuery query = testSet(Constants.Set.BASE64_PART_DELIMITER + enc("fq=title:laser&q=10.5072"), "title:laser");
        assertEquals("10.5072", query.getQuery());
        
        testSet("FOO" + Constants.Set.BASE64_PART_DELIMITER + enc("fq=title:laser"), "allocator_symbol:FOO", "title:laser");
    }

    private String enc(String str) {
        return Base64.encodeBase64URLSafeString(str.getBytes());
    }
    
}
