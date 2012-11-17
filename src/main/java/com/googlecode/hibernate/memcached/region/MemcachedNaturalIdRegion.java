/**
 * 
 */
package com.googlecode.hibernate.memcached.region;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.MemcachedCacheProvider;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedNaturalIdRegionAccessStrategy;

/**
 * @author Ruqsana Nazneen
 *
 */
public class MemcachedNaturalIdRegion extends AbstractMemcachedRegion implements NaturalIdRegion{

	private final Logger log = LoggerFactory.getLogger(MemcachedCacheProvider.class);

    private final CacheDataDescription metadata;
    private final Settings settings;
    
    public MemcachedNaturalIdRegion(MemcachedCache cache, Settings settings, CacheDataDescription metadata, Properties properties, Memcache client) {
        super(cache);
        this.metadata = metadata;
        this.settings = settings;
    }
	
	@Override
	public CacheDataDescription getCacheDataDescription() {
		return this.metadata;
	}

	@Override
	public boolean isTransactionAware() {
		return true;
	}

	@Override
	public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType)
			throws CacheException {
		
		if (AccessType.READ_ONLY.equals(accessType)) {
            if (metadata.isMutable()) {
                log.warn("read-only cache configured for mutable entity ["
                                + getName() + "]");
            }
            return new ReadOnlyMemcachedNaturalIdRegionAccessStrategy(this ,
                    settings);
        } else if (AccessType.READ_WRITE.equals(accessType)) {
            return new ReadWriteMemcachedNaturalIdRegionAccessStrategy(this ,
                    settings);
        } else if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
            return new NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(
                    this , settings);
        } else if (AccessType.TRANSACTIONAL.equals(accessType)) {
            return new TransactionalMemcachedNaturalIdRegionAccessStrategy(
                    this , cache, settings);
        } else {
            throw new IllegalArgumentException(
                    "unrecognized access strategy type [" + accessType
                            + "]");
        }
	}

}
