/* Copyright 2008 Ray Krueger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.hibernate.memcached.region;

import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedCollectionRegionAccessStrategy;
import java.util.Properties;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kcarlson
 */
public class MemcachedCollectionRegion extends AbstractMemcachedRegion implements CollectionRegion {
    
    private final Logger log = LoggerFactory.getLogger(MemcachedCollectionRegion.class);
    private final CacheDataDescription metadata;
    private final Settings settings;

    public MemcachedCollectionRegion(MemcachedCache cache, Settings settings, CacheDataDescription metadata, Properties properties, Memcache client)
    {
        super(cache);
        this.metadata = metadata;
        this.settings = settings;
    }


    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException
    {
        if (AccessType.READ_ONLY.equals(accessType)) {
            if (metadata.isMutable()) {
                log.warn("read-only cache configured for mutable entity ["
                                + getName() + "]");
            }
            return new ReadOnlyMemcachedCollectionRegionAccessStrategy(this ,
                    settings);
        } else if (AccessType.READ_WRITE.equals(accessType)) {
            return new ReadWriteMemcachedCollectionRegionAccessStrategy(this ,
                    settings);
        } else if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
            return new NonStrictReadWriteMemcachedCollectionRegionAccessStrategy(
                    this , settings);
        } else if (AccessType.TRANSACTIONAL.equals(accessType)) {
            return new TransactionalMemcachedCollectionRegionAccessStrategy(
                    this , cache, settings);
        } else {
            throw new IllegalArgumentException(
                    "unrecognized access strategy type [" + accessType
                            + "]");
        }
    }

    public boolean isTransactionAware()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CacheDataDescription getCacheDataDescription()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}