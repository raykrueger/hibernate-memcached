/* Copyright 2008 Ray Krueger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.hibernate.memcached.strategy;

import com.googlecode.hibernate.memcached.region.MemcachedCollectionRegion;
import com.googlecode.hibernate.memcached.strategy.AbstractMemcachedAccessStrategy;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 *
 * @author kcarlson
 */
abstract class AbstractCollectionRegionAccessStrategy
                    extends AbstractMemcachedAccessStrategy<MemcachedCollectionRegion> implements CollectionRegionAccessStrategy
{
    
    protected AbstractCollectionRegionAccessStrategy(MemcachedCollectionRegion collectionRegion, Settings settings)
    {
        super(collectionRegion, settings);
    }

    public CollectionRegion getRegion()
    {
        return region;
    }
    
}
