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

import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 *
 * @author kcarlson
 */
public class ReadOnlyMemcachedEntityRegionAccessStrategy extends AbstractEntityRegionAccessStrategy
{

    public ReadOnlyMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion aThis, Settings settings)
    {
        super(aThis, settings);
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Object key, long txTimestamp) throws CacheException {
        return region.getCache().get(key);
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException {
        if (minimalPutOverride && region.getCache().get(key) != null) {
            return false;
        } else {
            region.getCache().put(key, value);
            return true;
        }
    }
 
    /**
     * Throws UnsupportedOperationException since this cache is read-only
     *
     * @throws UnsupportedOperationException always
     */
    public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }
 
    /**
     * A no-op since this cache is read-only
     */
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        //throw new UnsupportedOperationException("Can't write to a readonly object");
    }
 
    /**
     * This cache is asynchronous hence a no-op
     */
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        region.getCache().put(key, value);
        return true;
    }
 
    /**
     * Throws UnsupportedOperationException since this cache is read-only
     *
     * @throws UnsupportedOperationException always
     */
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }
 
    /**
     * Throws UnsupportedOperationException since this cache is read-only
     *
     * @throws UnsupportedOperationException always
     */
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }
    
}
