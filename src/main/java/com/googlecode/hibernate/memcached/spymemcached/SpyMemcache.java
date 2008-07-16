package com.googlecode.hibernate.memcached.spymemcached;

import com.googlecode.hibernate.memcached.Memcache;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class SpyMemcache implements Memcache {

    private static final Logger log = LoggerFactory.getLogger(SpyMemcache.class);

    private final MemcachedClient memcachedClient;

    public SpyMemcache(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public Object get(String key) {
        try {
            log.debug("MemcachedClient.get({})", key);
            return memcachedClient.get(key);
        } catch (OperationTimeoutException e) {
            log.warn("Cache 'get' timed out for key [" + key + "]", e);
        }
        return null;
    }

    public void set(String key, int cacheTimeSeconds, Object o) {
        log.debug("MemcachedClient.set({})", key);
        try {
            memcachedClient.set(key, cacheTimeSeconds, o);
        } catch (OperationTimeoutException e) {
            log.warn("Cache 'set' timed out for key [" + key + "]", e);
        }
    }

    public void delete(String key) {
        try {
            memcachedClient.delete(key);
        } catch (OperationTimeoutException e) {
            log.warn("Cache 'delete' timed out for key [" + key + "]", e);
        }
    }

    public void incr(String key, int factor, int startingValue) {
        try {
            memcachedClient.incr(key, factor, startingValue);
        } catch (OperationTimeoutException e) {
            log.warn("Cache 'incr' timed out for key [" + key + "]", e);
        }
    }

    public void shutdown() {
        log.debug("Shutting down spy MemcachedClient");
        memcachedClient.shutdown();
    }
}
