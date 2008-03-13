package com.googlecode.hibernate.memcached;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClient;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class MemcachedCache implements Cache {

    private final String namespace;
    private final MemcachedClient memcachedClient;
    private long asynchGetTimeoutMillis = 500;
    private int cacheTimeSeconds = 300;

    public MemcachedCache(String namespace, MemcachedClient memcachedClient) {
        this.namespace = namespace;
        this.memcachedClient = memcachedClient;
    }

    private Object memcacheGet(Object key) {
        try {
            return memcachedClient.asyncGet(toKey(key)).get(asynchGetTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        } catch (TimeoutException e) {
            return null;
        }
    }

    private void memcacheSet(Object key, Object o) {
        memcachedClient.set(toKey(key), cacheTimeSeconds, o);
    }

    private String toKey(Object key) {
        return namespace + ":" + String.valueOf(key).replace(' ', '_');
    }

    public Object read(Object key) throws CacheException {
        return memcacheGet(key);
    }

    public Object get(Object key) throws CacheException {
        return memcacheGet(key);
    }

    public void put(Object key, Object value) throws CacheException {
        memcacheSet(key, value);
    }

    public void update(Object key, Object value) throws CacheException {
        memcacheSet(key, value);
    }

    public void remove(Object key) throws CacheException {
        memcachedClient.delete(toKey(key));
    }

    public void clear() throws CacheException {
        //what to do, what to do...
    }

    public void destroy() throws CacheException {
    }

    public void lock(Object key) throws CacheException {
    }

    public void unlock(Object key) throws CacheException {
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }

    public int getTimeout() {
        return 60000;//no idea
    }

    public String getRegionName() {
        return namespace;
    }

    public long getSizeInMemory() {
        return -1;
    }

    public long getElementCountInMemory() {
        return -1;
    }

    public long getElementCountOnDisk() {
        return -1;
    }

    public Map toMap() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "Memcached (" + namespace + ")";
    }
}
