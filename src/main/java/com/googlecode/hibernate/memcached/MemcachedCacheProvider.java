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

import net.spy.memcached.MemcachedClient;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <tr><td>hibernate.memcached.keyStrategy</td><td>{@link HashCodeKeyStrategy}</td>
 * <td>Sets the strategy class to to use for generating cache keys.
 * Must provide a class name that implements {@link com.googlecode.hibernate.memcached.KeyStrategy}</td></tr>
 * <tr><td>hibernate.memcached.readBufferSize</td>
 * <td>{@link net.spy.memcached.DefaultConnectionFactory#DEFAULT_READ_BUFFER_SIZE}</td>
 * <td>The read buffer size for each server connection from this factory</td></tr>
 * <tr><td>hibernate.memcached.operationQueueLength</td>
 * <td>{@link net.spy.memcached.DefaultConnectionFactory#DEFAULT_OP_QUEUE_LEN}</td>
 * <td>Maximum length of the operation queue returned by this connection factory</td></tr>
 * <tr><td>hibernate.memcached.operationTimeout</td>
 * <td>{@link net.spy.memcached.DefaultConnectionFactory#DEFAULT_OPERATION_TIMEOUT}</td>
 * <td>Default operation timeout in milliseconds</td></tr>
 * <tr><td>hibernate.memcached.hashAlgorithm</td><td>{@link net.spy.memcached.HashAlgorithm#KETAMA_HASH}</td>
 * <td>Which hash algorithm to use when adding items to the cache.<br/>
 * <b>Note:</b> the MemcachedClient defaults to using
 * {@link net.spy.memcached.HashAlgorithm#NATIVE_HASH}, while the hibernate-memcached cache defaults to KETAMA_HASH
 * for "consistent hashing"</td></tr>
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
 * <td>none, see hibernate.memcached.cacheTimeSeconds</td>
 * <td>Set the cache time for this cache region, overriding the cache-wide setting.</td></tr>
 * <tr><td>hibernate.memcached.[region-name].keyStrategy</td><td>none, see hibernate.memcached.keyStrategy</td>
 * <td>Overrides the strategy class to to use for generating cache keys in this cache region.
 * Must provide a class name that implements {@link com.googlecode.hibernate.memcached.KeyStrategy}</td></tr>
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

    private final Logger log = LoggerFactory.getLogger(MemcachedCacheProvider.class);

    private MemcachedClient client;

    public static final int DEFAULT_CACHE_TIME_SECONDS = 300;
    public static final boolean DEFAULT_CLEAR_SUPPORTED = false;

    public static final String PROP_PREFIX = "hibernate.memcached.";
    public static final String PROP_CACHE_TIME_SECONDS = PROP_PREFIX + "cacheTimeSeconds";
    public static final String PROP_CLEAR_SUPPORTED = PROP_PREFIX + "clearSupported";

    public Cache buildCache(String regionName, Properties properties) throws CacheException {

        PropertiesHelper props = new PropertiesHelper(properties);

        log.info("Building cache for region [{}]", regionName);

        MemcachedCache cache = new MemcachedCache(regionName, client);

        int defaultCacheTimeSeconds = getDefaultCacheTimeSeconds(props);
        boolean defaultClearSupported = getDefaultClearSupported(props);

        String regionPrefix = PROP_PREFIX + regionName + ".";

        String keyStrategy = getKeyStrategyName(props, regionPrefix);
        if (keyStrategy != null) {
            setKeyStrategy(keyStrategy, cache);
        }

        int cacheTime = props.getInt(regionPrefix + "cacheTimeSeconds",
                defaultCacheTimeSeconds);
        cache.setCacheTimeSeconds(cacheTime);

        boolean clearSupported = props.getBoolean(regionPrefix + "clearSupported",
                defaultClearSupported);
        cache.setClearSupported(clearSupported);

        return cache;
    }

    private String getKeyStrategyName(PropertiesHelper properties, String regionPrefix) {
        return properties.findValue(PROP_PREFIX + "keyStrategy", regionPrefix + "keyStrategy");
    }

    private void setKeyStrategy(String keyStrategyName, MemcachedCache cache) {
        log.debug("Using KeyStrategy: [{}]", keyStrategyName);
        KeyStrategy keyStrategy = instantiateKeyStrategy(keyStrategyName);
        cache.setKeyStrategy(keyStrategy);
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

    private boolean getDefaultClearSupported(PropertiesHelper properties) {
        return properties.getBoolean(PROP_CLEAR_SUPPORTED, DEFAULT_CLEAR_SUPPORTED);
    }

    private int getDefaultCacheTimeSeconds(PropertiesHelper props) {
        return props.getInt(PROP_CACHE_TIME_SECONDS, DEFAULT_CACHE_TIME_SECONDS);
    }

    /**
     * No clue what this is for, Hibernate docs don't say.
     *
     * @return long {@link org.hibernate.cache.Timestamper#next()}
     */
    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    public void start(Properties properties) throws CacheException {
        log.info("Starting MemcachedClient...");
        try {
            client = getMemcachedClientFactory(properties).createMemcachedClient();
        } catch (Exception e) {
            throw new CacheException("Unable to initialize MemcachedClient", e);
        }
    }

    protected MemcachedClientFactory getMemcachedClientFactory(Properties properties) {
        return new DefaultMemcachedClientFactory(properties);
    }

    public void stop() {
        if (client != null) {
            log.debug("Shutting down MemcachedClient");
            client.shutdown();
        }
        client = null;
    }

    /**
     * According to the hibernate reference docs, MinimalPutsEnabledByDefault should be true for distributed caches.
     *
     * @return true
     */
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }
}