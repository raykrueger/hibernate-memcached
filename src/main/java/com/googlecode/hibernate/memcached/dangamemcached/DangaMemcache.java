package com.googlecode.hibernate.memcached.dangamemcached;

import java.util.Calendar;
import java.util.Date;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import com.googlecode.hibernate.memcached.Memcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DOCUMENT ME!
 *
 * @author George Wei
 */
public class DangaMemcache implements Memcache {

    private static final Logger log = LoggerFactory.getLogger(DangaMemcache.class);

    private final MemCachedClient memcachedClient;
    private final String poolName;

    /* Constructor
     *
     * @param memcachedClient Instance of Danga's MemCachedClient
     * @param poolName SockIOPool name used to instantiate memcachedClient
     */
    public DangaMemcache(MemCachedClient memcachedClient, String poolName) {
        this.memcachedClient = memcachedClient;
        this.poolName = poolName;
    }

    public Object get(String key) {
        try {
            log.debug("MemCachedClient.get({})", key);
            return memcachedClient.get(key);
        } catch (Exception e) {
            log.warn("Cache 'get' timed out for key [" + key + "]", e);
        }
        return null;
    }

    public void set(String key, int cacheTimeSeconds, Object o) {
        log.debug("MemCachedClient.set({})", key);
        try {
		        Calendar calendar = Calendar.getInstance();
		        calendar.setTime(new Date());
		        calendar.add(Calendar.SECOND, cacheTimeSeconds);

            memcachedClient.set(key, o, calendar.getTime());
        } catch (Exception e) {
            log.warn("Cache 'set' timed out for key [" + key + "]", e);
        }
    }

    public void delete(String key) {
        try {
            memcachedClient.delete(key);
        } catch (Exception e) {
            log.warn("Cache 'delete' timed out for key [" + key + "]", e);
        }
    }

    public void incr(String key, int factor, int startingValue) {
        try {
            //Try to incr
            long rv = memcachedClient.incr(key, factor);

            //If the key is not found, add it with startingValue
            if (-1 == rv)
                memcachedClient.addOrIncr(key, startingValue);
        } catch (Exception e) {
            log.warn("Cache 'incr' timed out for key [" + key + "]", e);
        }
    }

    public void shutdown() {
        log.debug("Shutting down danga MemCachedClient");

        //Danga's MemCachedClient does not provide a method to shutdown or
        //close it, let's shutdown its SockIOPool instead
        SockIOPool.getInstance(poolName).shutDown();
    }
}
