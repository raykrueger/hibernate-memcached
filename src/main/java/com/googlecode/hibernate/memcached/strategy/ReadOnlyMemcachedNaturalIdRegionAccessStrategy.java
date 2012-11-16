/**
 * 
 */
package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

/**
 * @author Ruqsana Nazneen
 *
 */
public class ReadOnlyMemcachedNaturalIdRegionAccessStrategy extends AbstractNaturalIdRegionAccessStrategy {

	public ReadOnlyMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion aThis, Settings settings)
    {
        super(aThis, settings);
    }
	
	@Override
	public boolean afterInsert(Object key, Object value) throws CacheException {
		 region.getCache().put(key, value);
	     return true;
	}

	@Override
	public boolean afterUpdate(Object key, Object value, SoftLock arg2)
			throws CacheException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	@Override
	public boolean insert(Object arg0, Object arg1) throws CacheException {
		return false;
	}

	@Override
	public boolean update(Object arg0, Object arg1) throws CacheException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	@Override
	public Object get(Object arg0, long key) throws CacheException {
		return region.getCache().get(key);
	}

	@Override
	public SoftLock lockItem(Object arg0, Object arg1) throws CacheException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	@Override
	public void unlockItem(Object arg0, SoftLock arg1) throws CacheException {
		return;
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version, boolean minimalPutOverride) throws CacheException {
		
		if (minimalPutOverride && region.getCache().get(key) != null) {
            return false;
        } else {
            region.getCache().put(key, value);
            return true;
        }
	}

}
