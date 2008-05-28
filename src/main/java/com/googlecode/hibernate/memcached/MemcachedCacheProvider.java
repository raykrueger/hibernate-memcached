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
 * DOCUMENT ME!
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

        String keyStrategyClassProp =
                properties.getProperty(PROP_PREFIX + "keyStrategy");
        if (keyStrategyClassProp != null) {
            KeyStrategy keyStrategy = instantiateKeyStrategy(properties.getProperty(keyStrategyClassProp));
            cache.setKeyStrategy(keyStrategy);
        }

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

    private KeyStrategy instantiateKeyStrategy(String cls) {
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
                properties.getProperty(PROP_PREFIX + "deafultClearSupported");
        if (defaultClearSupportedProp != null) {
            defaultClearSupported = Boolean.parseBoolean(defaultClearSupportedProp);
        }
        return defaultClearSupported;
    }

    private int getDefaultCacheTimeSeconds(Properties properties) {
        int defaultCacheTimeSeconds = DEFAULT_CACHE_TIME_SECONDS;
        String defaultCacheTimeSecondsProp =
                properties.getProperty(PROP_PREFIX + "defaultCacheTimeSeconds");
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
