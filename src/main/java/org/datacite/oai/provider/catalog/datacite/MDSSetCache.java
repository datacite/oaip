package datacite.oai.provider.catalog.datacite;

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

import java.util.LinkedList;
import java.util.List;

import datacite.oai.provider.ApplicationContext;
import datacite.oai.provider.Constants;
import datacite.oai.provider.service.MDSSearchService;
import datacite.oai.provider.service.ServiceCollection;
import datacite.oai.provider.util.Pair;

/**
 * Class encapsulating a cache of set records.
 * @author PaluchM
 *
 */
public class MDSSetCache {

    // Singleton instance
    private static MDSSetCache instance;

    // Cache time stamps
    private long lastRefresh;
    private long expiryInterval;

    // Cache data structures
    private List<SetRecordBean> setlist;

    /**
     * Initialization method for the set cache.
     * @throws Exception
     */
    public static void initialize() throws Exception {
        instance = new MDSSetCache();
    }

    /**
     * Returns an instance of the set cache.
     * @return set cache instance.
     */
    public static MDSSetCache getInstance() {
        return instance;
    }

    // Private constructor
    private MDSSetCache() throws Exception {

        ApplicationContext context = ApplicationContext.getInstance();

        // Convert to milliseconds
        expiryInterval = 1000 * context.getIntProperty(Constants.Property.MDS_SETCACHE_EXPIRY_SECONDS);
        lastRefresh = 0;
    }

    // refreshes the cache if it is expired
    private void refreshCache() throws Exception {

        long now = System.currentTimeMillis();
        if (lastRefresh < now - expiryInterval) {

            synchronized (this) {
                if (lastRefresh < now - expiryInterval) {
                    refreshCacheImpl();
                    lastRefresh = now;
                }
            }
        }
    }

    private void refreshCacheImpl() throws Exception {
        setlist = new LinkedList<SetRecordBean>();

        //Add the basic reference quality set
        setlist.add(new SetRecordBean(Constants.Set.REF_QUALITY,"Reference quality citations only."));
        
        ServiceCollection services = ServiceCollection.getInstance();
        MDSSearchService mdsService = services.getMDSSearchService();
        Pair<List<SetRecordBean>,Integer> result = mdsService.getSets();

        List<SetRecordBean> newSets = result.getFirst(); 
        
        if (newSets != null) {
            for (SetRecordBean set : newSets) {
                setlist.add(set);
                setlist.add(set.createRefQualitySet());
            }
        }
        
    }

    /**
     * Returns all sets from the cache.
     * @return all sets from the cache as a list.
     * @throws Exception
     */
    public List< SetRecordBean > getSetList() throws Exception {
        refreshCache();
        return setlist;
    }
    
    /**
     * Returns a result containing the requested sets from the cache and the total cache size.
     * @param offset The offset at which to start returning sets.
     * @param length The number of sets to return.
     * @return a pair object containing the requested sets from the cache and the total cache size.
     * @throws Exception
     */
    public Pair< List< SetRecordBean >, Integer > getSets(int offset, int length) throws Exception {
        refreshCache();

        if (setlist == null) {
            return null;
        }

        if (offset < 0 || length <= 0) {
            return null;
        }

        if (offset >= setlist.size()) {
            return null;
        }

        int toIndex = Math.min(offset + length, setlist.size());
        List< SetRecordBean > sublist = setlist.subList(offset, toIndex);
        Integer fullSize = setlist.size();

        return new Pair< List< SetRecordBean >, Integer >(sublist, fullSize);
    }
}
