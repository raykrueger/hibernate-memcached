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
public class NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy extends AbstractNaturalIdRegionAccessStrategy{

	public NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion aThis, Settings settings)
    {
        super(aThis, settings);
    }
	
	@Override
	public boolean afterInsert(Object arg0, Object arg1) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean afterUpdate(Object key, Object currentVersion, SoftLock lock)
			throws CacheException {
		
		unlockItem(key, lock);
        return false;
	}

	@Override
	public boolean insert(Object arg0, Object arg1) throws CacheException {
		return false;
	}

	@Override
	public boolean update(Object key, Object arg1) throws CacheException {
		region.getCache().remove(key);
        return false;
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		return region.getCache().get(key);
	}

	@Override
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return null;
	}

	@Override
	public void unlockItem(Object key, SoftLock arg1) throws CacheException {
		 region.getCache().remove(key);
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
