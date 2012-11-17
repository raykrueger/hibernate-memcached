/*
 * -----------------------------------------------------------------------------
 * Copyright (C) 2008-2011 by Bloo AB
 * SWEDEN, e-mail: info@bloo.se
 *
 * This program may be used and/or copied only with the written permission
 * from Bloo AB, or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the program
 * has been supplied.
 *
 * All rights reserved.
 *
 * -----------------------------------------------------------------------------
 */
package com.googlecode.hibernate.memcached.strategy;

import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.SoftLock;

import org.hibernate.cfg.Settings;

/**
 *
 * @author kcarlson
 */
public class TransactionalMemcachedEntityRegionAccessStrategy extends AbstractEntityRegionAccessStrategy
{

    private final MemcachedCache cache;
    public TransactionalMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion aThis, MemcachedCache cache, Settings settings)
    {
        super(aThis, settings);
        this.cache = cache;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean afterInsert(Object key, Object value, Object version) {
        return false;
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        return false;
    }
 
    /**
     * {@inheritDoc}
     */
    public Object get(Object key, long txTimestamp) throws CacheException {
        return cache.get(key);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegion getRegion() {
        return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean insert(Object key, Object value, Object version)
            throws CacheException {
        //OptimisticCache? versioning?
        cache.put(key, value);
        return true;
    }
 
    /**
     * {@inheritDoc}
     */
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean putFromLoad(Object key, Object value, long txTimestamp,
                               Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && cache.get(key) != null) {
            return false;
        }
        //OptimisticCache? versioning?
        cache.put(key, value);
        return true;
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Object key) throws CacheException {
        cache.remove(key);
    }
 
    /**
     * {@inheritDoc}
     */
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // no-op
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean update(Object key, Object value, Object currentVersion,
                          Object previousVersion) throws CacheException {
        cache.put(key, value);
        return true;
    }
}
