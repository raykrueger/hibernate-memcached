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
public class NonStrictReadWriteMemcachedEntityRegionAccessStrategy extends AbstractEntityRegionAccessStrategy
{

    public NonStrictReadWriteMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion aThis, Settings settings)
    {
        super(aThis, settings);
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException
    {
        if (minimalPutOverride && region.getCache().get(key) != null) {
            return false;
        } else {
            region.getCache().put(key, value);
            return true;
        }
    }

    public boolean insert(Object key, Object value, Object version) throws CacheException
    {
        return false;
    }

    public boolean afterInsert(Object key, Object value, Object version) throws CacheException
    {
//        region.getCache().put(key, value);
        return false;
    }

    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException
    {
        region.getCache().remove(key);
        return false;
    }

    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException
    {
        unlockItem(key, lock);
        return false;
    }

    public Object get(Object key, long txTimestamp) throws CacheException
    {
        return region.getCache().get(key);
    }

    public SoftLock lockItem(Object key, Object version) throws CacheException
    {
        return null;
    }

    public void unlockItem(Object key, SoftLock lock) throws CacheException
    {
        region.getCache().remove(key);
    }
    
}
