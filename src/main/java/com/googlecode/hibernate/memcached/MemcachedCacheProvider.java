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

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;

import java.io.IOException;
import java.util.Properties;

/**
 * Configures an instance of {@link MemcachedCache} for use as a second-level cache in Hibernate.
 * To use set the hibernate property <i>hibernate.cache.provider_class</i> to the name of this class.
 * <p/>
 * There are two types of property settings that the MemcachedCacheProvider supports, cache-wide properties
 * and region-name properties.
 * <p/>
 * <b>Cache wide properties</b>
 * <table border='1'>
 * <tr><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr><td>hibernate.memcached.servers</td><td>localhost:11211</td>
 * <td>Space delimited list of memcached instances in host:port format</td></tr>
 * <tr><td>hibernate.memcached.cacheTimeSeconds</td><td>300</td>
 * <td>The default number of seconds items should be cached. Can be overriden at the regon level.</td></tr>
 * <tr><td>hibernate.memcached.keyStrategy</td><td>{@link DefaultKeyStrategy}</td>
 * <td>Sets the strategy class to to use for generating cache keys.
 * Must provide a class name that implements {@link com.googlecode.hibernate.memcached.KeyStrategy}</td></tr>
 * <tr><td>hibernate.memcached.clearSupported</td><td>false</td>
 * <td>Enables support for the {@link MemcachedCache#clear()} method for all cache regions.
 * The way clear is implemented for memcached is expensive and adds overhead to all get/set operations.
 * It is not recommended for production use.</td></tr>
 * </table>
 * <p/>
 * <b>Cache Region properties</b><br/>
 * Cache regon properties are set by giving your cached data a "region name" in hibernate.
 * You can tune the MemcachedCache instance for your region using the following properties.
 * These properties essentially override the cache-wide properties above.<br/>
 * <table border='1'>
 * <tr><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr><td>hibernate.memcached.[region-name].cacheTimeSeconds</td>
 * <td>none see hibernate.memcached.cacheTimeSeconds</td>
 * <td>Set the cache time for this cache region, overriding the cache-wide setting.</td></tr>
 * <tr><td>hibernate.memcached.[region-name].clearSupported</td>
 * <td>none, see hibernate.memcached.clearSupported</td>
 * <td>Enables clear() operations for this cache region only.
 * Again, the clear operation incurs cost on every get/set operation.</td>
 * </tr>
 * </table>
 *
 * @author Ray Krueger
 */
public class MemcachedCacheProvider implements CacheProvider {

    private static final Log log = LogFactory.getLog(MemcachedCacheProvider.class);

    private MemcachedClient client;
    public static final int DEFAULT_CACHE_TIME_SECONDS = 300;
    public static final boolean DEFAULT_CLEAR_SUPPORTED = false;

    public static final String PROP_PREFIX = "hibernate.memcached.";
    public static final String PROP_SERVERS = PROP_PREFIX + "servers";

    public Cache buildCache(String regionName, Properties properties) throws CacheException {
        MemcachedCache cache = new MemcachedCache(regionName, client);

        int defaultCacheTimeSeconds = getDefaultCacheTimeSeconds(properties);
        boolean defaultClearSupported = getDefaultClearSupported(properties);

        setKeyStrategy(properties, cache);

        String regionPrefix = PROP_PREFIX + regionName + ".";

        String propCacheTimeSeconds = regionPrefix + "cacheTimeSeconds";
        if (properties.containsKey(propCacheTimeSeconds)) {
            cache.setCacheTimeSeconds(Integer.valueOf(properties.getProperty(propCacheTimeSeconds)));
        } else {
            cache.setCacheTimeSeconds(defaultCacheTimeSeconds);
        }

        String propClearSupported = regionPrefix + "clearSupported";
        if (properties.containsKey(propClearSupported)) {
            cache.setClearSupported(Boolean.valueOf(properties.getProperty(propClearSupported)));
        } else {
            cache.setClearSupported(defaultClearSupported);
        }

        return cache;
    }

    private void setKeyStrategy(Properties properties, MemcachedCache cache) {
        String keyStrategyClassProp =
                properties.getProperty(PROP_PREFIX + "keyStrategy");
        if (keyStrategyClassProp != null) {
            KeyStrategy keyStrategy = instantiateKeyStrategy(keyStrategyClassProp);
            cache.setKeyStrategy(keyStrategy);
            log.debug("Using KeyStrategy instance: [" + keyStrategy + "]");
        }
    }

    protected KeyStrategy instantiateKeyStrategy(String cls) {
        try {
            return (KeyStrategy) Class.forName(cls).newInstance();
        } catch (InstantiationException e) {
            throw new CacheException("Could not instantiate keyStrategy class", e);
        } catch (IllegalAccessException e) {
            throw new CacheException("Could not instantiate keyStrategy class", e);
        } catch (ClassNotFoundException e) {
            throw new CacheException("Could not instantiate keyStrategy class", e);
        }
    }

    private boolean getDefaultClearSupported(Properties properties) {
        boolean defaultClearSupported = DEFAULT_CLEAR_SUPPORTED;
        String defaultClearSupportedProp =
                properties.getProperty(PROP_PREFIX + "clearSupported");
        if (defaultClearSupportedProp != null) {
            defaultClearSupported = Boolean.parseBoolean(defaultClearSupportedProp);
        }
        return defaultClearSupported;
    }

    private int getDefaultCacheTimeSeconds(Properties properties) {
        int defaultCacheTimeSeconds = DEFAULT_CACHE_TIME_SECONDS;
        String defaultCacheTimeSecondsProp =
                properties.getProperty(PROP_PREFIX + "cacheTimeSeconds");
        if (defaultCacheTimeSecondsProp != null) {
            defaultCacheTimeSeconds = Integer.parseInt(defaultCacheTimeSecondsProp);
        }
        return defaultCacheTimeSeconds;
    }

    /**
     * No clue what this is for, Hibernate docs don't say.
     *
     * @return long {@link org.hibernate.cache.Timestamper#next()}
     */
    public long nextTimestamp() {
        return Timestamper.next();
    }

    public void start(Properties properties) throws CacheException {
        String serverList = properties.getProperty(PROP_SERVERS, "localhost:11211");

        if (log.isDebugEnabled()) {
            log.debug("Starting MemcachedClient with serverList [" + serverList + "]");
        }

        try {
            client = createMemcachedClient(serverList, properties);
        } catch (IOException e) {
            throw new CacheException("Unable to initialize MemcachedClient using serverList [" + serverList + "]");
        }
    }

    /**
     * Given the serverList and access to the cache properties, create the MemcachedClient to use.
     *
     * @param serverList space delimeted serverlist container host and port. Ex:"host:11211 otherhost:11211"
     * @param properties Properties from {@link #start(java.util.Properties)}
     * @return MemcachedClient
     * @throws IOException if the MemcachedClient throws it
     */
    protected MemcachedClient createMemcachedClient(String serverList, Properties properties) throws IOException {
        return new MemcachedClient(AddrUtil.getAddresses(serverList));
    }

    public void stop() {
        if (client != null) {
            log.debug("Shutting down MemcachedClient");
            client.shutdown();
        }
        client = null;
    }

    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }
}
