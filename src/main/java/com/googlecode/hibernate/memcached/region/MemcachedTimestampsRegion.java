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
import com.googlecode.hibernate.memcached.MemcachedCacheProvider;
import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.TimestampsRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author kcarlson
 */
public class MemcachedTimestampsRegion extends AbstractMemcachedRegion implements TimestampsRegion {
    
    private final Logger log = LoggerFactory.getLogger(MemcachedCacheProvider.class);

    public MemcachedTimestampsRegion(MemcachedCache cache, Properties properties, Memcache client) {
        super(cache);
    }

    public Object get(Object key) throws CacheException {
        return cache.get(key);
    }

    public void put(Object key, Object value) throws CacheException {
        cache.put(key, value);
    }

    public void evict(Object key) throws CacheException {
        cache.remove(key);
    }

    public void evictAll() throws CacheException {
        cache.clear();
    }


}
