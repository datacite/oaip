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

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Extension to generic pool to initialize pool sizes, and testing strategy.
 * @author PaluchM
 */
public class CustomConnPool extends GenericObjectPool implements ObjectPool{

    public CustomConnPool(int minSize,int maxSize){
        super();
        this.setMaxActive(maxSize);
        this.setMinIdle(minSize);
        this.setWhenExhaustedAction(WHEN_EXHAUSTED_FAIL);
        this.setTestOnBorrow(true);
    }
}
