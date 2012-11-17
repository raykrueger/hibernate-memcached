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
package com.googlecode.hibernate.memcached;

import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.region.MemcachedCollectionRegion;
import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;
import com.googlecode.hibernate.memcached.region.MemcachedQueryResultsRegion;
import com.googlecode.hibernate.memcached.region.MemcachedTimestampsRegion;


/**
 *
 * @author kcarlson
 */
public class MemcachedRegionFactory implements RegionFactory {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6623443278469107548L;

	private final Logger log = LoggerFactory.getLogger(MemcachedRegionFactory.class);
    
    private final ConcurrentMap<String, MemcachedCache> caches = new ConcurrentHashMap<String, MemcachedCache>();
    
    private Properties properties;
    private Memcache client;
    private Settings settings;
    
    public MemcachedRegionFactory(Properties properties) {
        this.properties = properties;
    }
    
    public MemcachedRegionFactory() {
    }
    
    public void start(Settings settings, Properties properties) throws CacheException {
        this.settings = settings;
        this.properties = properties;
        log.info("Starting MemcachedClient...");
        try {
            client = getMemcachedClientFactory(new Config(new PropertiesHelper(properties)))
                    .createMemcacheClient();
        } catch (Exception e) {
            throw new CacheException("Unable to initialize MemcachedClient", e);
        }
    }

    public void stop() {
        if (client != null) {
            log.debug("Shutting down Memcache client");
            client.shutdown();
        }
        client = null;
    }

    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    public AccessType getDefaultAccessType() {
         return AccessType.READ_WRITE;
    }

    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new MemcachedEntityRegion(getCache(regionName), settings,
                        metadata, properties, client);
    }

    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new MemcachedCollectionRegion(getCache(regionName), settings,
                        metadata, properties, client);
    }

    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new MemcachedQueryResultsRegion(getCache(regionName),
                properties, client);
    }

    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new MemcachedTimestampsRegion(getCache(regionName),
                properties, client);
    }
    

	@Override
	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties,
			CacheDataDescription metadata) throws CacheException {
		return new MemcachedNaturalIdRegion(getCache(regionName), settings,
                        metadata, properties, client);
	}
    
    protected MemcacheClientFactory getMemcachedClientFactory(Config config) {
        String factoryClassName = config.getMemcachedClientFactoryName();

        Constructor<?> constructor;
        try {
        	        	
            constructor = Class.forName(factoryClassName)
                    .getConstructor(PropertiesHelper.class);
        } catch (ClassNotFoundException e) {
            throw new CacheException(
                    "Unable to find factory class [" + factoryClassName + "]", e);
        } catch (NoSuchMethodException e) {
            throw new CacheException(
                    "Unable to find PropertiesHelper constructor for factory class [" + factoryClassName + "]", e);
        }

        MemcacheClientFactory clientFactory;
        try {
            clientFactory = (MemcacheClientFactory) constructor.newInstance(config.getPropertiesHelper());
        } catch (Exception e) {
            throw new CacheException(
                    "Unable to instantiate factory class [" + factoryClassName + "]", e);
        }

        return clientFactory;
    }
    
    private MemcachedCache getCache(String regionName)
    {
        return caches.get(regionName) == null
                ? new MemcachedCache(regionName, client) : caches.get(regionName);
    }

    public Memcache getClient() {
        return client;
    }

}
