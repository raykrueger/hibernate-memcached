/**
 * 
 */
package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

/**
 * @author Ruqsana Nazneen 
 *
 */
public abstract class AbstractNaturalIdRegionAccessStrategy 
	extends AbstractMemcachedAccessStrategy<MemcachedNaturalIdRegion> implements NaturalIdRegionAccessStrategy{

	protected AbstractNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion NaturalIdRegion, Settings settings)
    {
        super(NaturalIdRegion, settings);
    }

    public NaturalIdRegion getRegion()
    {
        return region;
    }
}
