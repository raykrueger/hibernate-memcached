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

    MemcachedClient client;
    public static final String PROP_SERVERS = "hibernate.memcached.servers";

    public Cache buildCache(String regionName, Properties properties) throws CacheException {
        MemcachedCache cache = new MemcachedCache(regionName, client);

        String memcachedPrefix = "hibernate.memcached." + regionName;

        String propAsynchGetTimeoutMillis = memcachedPrefix + ".asynchGetTimeoutMillis";
        if (properties.containsKey(propAsynchGetTimeoutMillis)) {
            cache.setAsynchGetTimeoutMillis(Long.valueOf(properties.getProperty(propAsynchGetTimeoutMillis)));
        }

        String propCacheTimeSeconds = memcachedPrefix + ".cacheTimeSeconds";
        if (properties.containsKey(propCacheTimeSeconds)) {
            cache.setCacheTimeSeconds(Integer.valueOf(properties.getProperty(propCacheTimeSeconds)));
        }

        String propClearSupported = memcachedPrefix + ".clearSupported";
        if (properties.containsKey(propClearSupported)) {
            cache.setClearSupported(Boolean.valueOf(properties.getProperty(propClearSupported)));
        }

        return cache;
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }

    public void start(Properties properties) throws CacheException {
        String serverList = properties.getProperty(PROP_SERVERS, "localhost:11211");
        try {
            client = new MemcachedClient(AddrUtil.getAddresses(serverList));
        } catch (IOException e) {
            throw new CacheException("Unable to initialize MemcachedClient using serverList [" + serverList + "]");
        }
    }

    public void stop() {
        if (client != null) {
            client.shutdown();
        }
        client = null;
    }

    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }
}
