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
package com.googlecode.hibernate.memcached.strategy;

import com.googlecode.hibernate.memcached.region.AbstractMemcachedRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 *
 * @author kcarlson
 */
public abstract class AbstractMemcachedAccessStrategy<T extends AbstractMemcachedRegion>
{
    protected T region;
    private Settings settings;
    
    public AbstractMemcachedAccessStrategy(T region, Settings settings) {
        this.region = region;
        this.settings = settings;
    }

    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException
    {
        return putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
    }

    public abstract boolean putFromLoad(Object key, Object value,
                long txTimestamp,Object version, boolean minimalPutOverride) throws CacheException;


    public SoftLock lockRegion() throws CacheException
    {
        return null;
    }

    public void unlockRegion(SoftLock lock) throws CacheException
    {
        region.getCache().clear();
    }

    public void remove(Object key) throws CacheException
    {
        
    }

    public void removeAll() throws CacheException
    {
        region.getCache().clear();
    }

    public void evict(Object key) throws CacheException
    {
        region.getCache().remove(key);
    }

    public void evictAll() throws CacheException
    {
        region.getCache().clear();
    }
    
}
