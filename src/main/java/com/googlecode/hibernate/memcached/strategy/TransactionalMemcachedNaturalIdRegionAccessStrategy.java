/**
 * 
 */
package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

/**
 * @author Ruqsana Nazneen
 *
 */
public class TransactionalMemcachedNaturalIdRegionAccessStrategy extends AbstractNaturalIdRegionAccessStrategy {

	private final MemcachedCache cache;
    public TransactionalMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion aThis, MemcachedCache cache, Settings settings)
    {
        super(aThis, settings);
        this.cache = cache;
    }
	
	@Override
	public boolean afterInsert(Object key, Object value) throws CacheException {
		return false;
	}

	@Override
	public boolean afterUpdate(Object key, Object value, SoftLock lock)
			throws CacheException {
		return false;
	}

	@Override
	public boolean insert(Object key, Object value) throws CacheException {
		cache.put(key, value);
        return true;
	}

	@Override
	public boolean update(Object key, Object value) throws CacheException {
		cache.put(key, value);
        return true;
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		return cache.get(key);
	}

	@Override
	public SoftLock lockItem(Object arg0, Object arg1) throws CacheException {
		return null;
	}

	@Override
	public void unlockItem(Object arg0, SoftLock arg1) throws CacheException {
		
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version, boolean minimalPutOverride) throws CacheException {
		if (minimalPutOverride && cache.get(key) != null) {
            return false;
        }
        //OptimisticCache? versioning?
        cache.put(key, value);
        return true;
	}

}
