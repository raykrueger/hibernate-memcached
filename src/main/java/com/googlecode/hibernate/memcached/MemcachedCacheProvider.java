package com.googlecode.hibernate.memcached;

import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;

import java.util.Properties;
import java.io.IOException;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.AddrUtil;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class MemcachedCacheProvider implements CacheProvider {

    MemcachedClient client;

    public Cache buildCache(String regionName, Properties properties) throws CacheException {
        return new MemcachedCache(regionName, client);
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }

    public void start(Properties properties) throws CacheException {
        String serverList = properties.getProperty("memcached.servers", "localhost:11211");
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
