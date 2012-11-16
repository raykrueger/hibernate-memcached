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

import com.googlecode.hibernate.memcached.region.AbstractMemcachedRegion;
import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import com.googlecode.hibernate.memcached.strategy.AbstractReadWriteMemcachedAccessStrategy;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 *
 * @author kcarlson
 */
public class ReadWriteMemcachedEntityRegionAccessStrategy 
        extends AbstractReadWriteMemcachedAccessStrategy<AbstractMemcachedRegion> implements EntityRegionAccessStrategy
{

    public ReadWriteMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion aThis, Settings settings)
    {
        super(aThis, settings, aThis.getCacheDataDescription());
    }

    public EntityRegion getRegion()
    {
        return (MemcachedEntityRegion)region;
    }

    public boolean insert(Object key, Object value, Object version) throws CacheException
    {
        return false;
    }

    public boolean afterInsert(Object key, Object value, Object version) throws CacheException
    {
        region.getCache().lock(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);
            if (item == null) {
                region.getCache().put(key, new Item(value, version, region.nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region.getCache().unlock(key);
        }
    }

    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException
    {
        return false;
    }

    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException
    {
        //what should we do with previousVersion here?
        region.getCache().lock(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);

            if (item != null && item.isUnlockable(lock)) {
                Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    region.getCache().put(key, new Item(value, currentVersion, region.nextTimestamp()));
                    return true;
                }
            } else {
                super.handleLockExpiry(key, null);
                return false;
            }
        } finally {
            region.getCache().unlock(key);
        }
    }
}
