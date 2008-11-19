package com.googlecode.hibernate.memcached.spymemcached;

import com.googlecode.hibernate.memcached.LoggingMemcacheExceptionHandler;
import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcacheExceptionHandler;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class SpyMemcache implements Memcache {

    private static final Logger log = LoggerFactory.getLogger(SpyMemcache.class);
    private MemcacheExceptionHandler exceptionHandler = new LoggingMemcacheExceptionHandler();

    private final MemcachedClient memcachedClient;

    public SpyMemcache(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public Object get(String key) {
        try {
            log.debug("MemcachedClient.get({})", key);
            return memcachedClient.get(key);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnGet(key, e);
        }
        return null;
    }

    public void set(String key, int cacheTimeSeconds, Object o) {
        log.debug("MemcachedClient.set({})", key);
        try {
            memcachedClient.set(key, cacheTimeSeconds, o);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnSet(key, cacheTimeSeconds, o, e);
        }
    }

    public void delete(String key) {
        try {
            memcachedClient.delete(key);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnDelete(key, e);
        }
    }

    public void incr(String key, int factor, int startingValue) {
        try {
            memcachedClient.incr(key, factor, startingValue);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnIncr(key, factor, startingValue, e);
        }
    }

    public void shutdown() {
        log.debug("Shutting down spy MemcachedClient");
        memcachedClient.shutdown();
    }

    public void setExceptionHandler(MemcacheExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
