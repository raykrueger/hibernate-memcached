/**
 * 
 */
package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.AbstractMemcachedRegion;
import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

/**
 * @author Ruqsanan Nazneen
 *
 */
public class ReadWriteMemcachedNaturalIdRegionAccessStrategy extends  AbstractReadWriteMemcachedAccessStrategy<AbstractMemcachedRegion> 
	implements NaturalIdRegionAccessStrategy
	{
	
	public ReadWriteMemcachedNaturalIdRegionAccessStrategy(
			MemcachedNaturalIdRegion naturalIdRegion, Settings settings) {
		super(naturalIdRegion, settings, naturalIdRegion.getCacheDataDescription());
	}

	@Override
	public boolean afterInsert(Object key, Object value) throws CacheException {
		region.getCache().lock( key );
		try {
			Lockable item = (Lockable) region.getCache().get( key );
			if ( item == null ) {
				region.getCache().put( key, new Item( value, null, region.nextTimestamp() ) );
				return true;
			}
			else {
				return false;
			}
		}
		finally {
			region.getCache().unlock( key );
		}
	}

	@Override
	public boolean afterUpdate(Object key, Object value, SoftLock lock)
			throws CacheException {
		region.getCache().lock( key );
		try {
			Lockable item = (Lockable) region.getCache().get( key );
			
			if ( item != null && item.isUnlockable( lock ) ) {
				Lock lockItem = (Lock) item;
				if ( lockItem.wasLockedConcurrently() ) {
					decrementLock( key, lockItem );
					return false;
				}
				else {
					region.getCache().put( key, new Item( value, null, region.nextTimestamp() ) );
					return true;
				}
			}
			else {
				handleLockExpiry( key, item );
				return false;
			}
		}
		finally {
			region.getCache().unlock( key );
		}
	}

	@Override
	public NaturalIdRegion getRegion() {
		return (MemcachedNaturalIdRegion) region;
	}

	@Override
	public boolean insert(Object arg0, Object arg1) throws CacheException {
		return false;
	}

	@Override
	public boolean update(Object arg0, Object arg1) throws CacheException {
		return false;
	}

	

}
