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
import com.googlecode.hibernate.memcached.strategy.AbstractMemcachedAccessStrategy;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 *
 * @author kcarlson
 */
abstract class AbstractEntityRegionAccessStrategy 
                    extends AbstractMemcachedAccessStrategy<MemcachedEntityRegion> implements EntityRegionAccessStrategy
{
    
    protected AbstractEntityRegionAccessStrategy(MemcachedEntityRegion entityRegion, Settings settings)
    {
        super(entityRegion, settings);
    }

    public EntityRegion getRegion()
    {
        return region;
    }
}
