package com.googlecode.hibernate.memcached.spymemcached;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.LoggingMemcacheExceptionHandler;
import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcacheExceptionHandler;
import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class SpyMemcache implements Memcache {

    private static final Logger log = LoggerFactory.getLogger(SpyMemcache.class);
    private MemcacheExceptionHandler exceptionHandler = new LoggingMemcacheExceptionHandler();

    private final MemcachedClient memcachedClient;
    private final ConnectionFactory connectionFactory;
    private final Long operationTimeout;
    private final boolean asyncWrites;

    public SpyMemcache(MemcachedClient memcachedClient) {
        this(memcachedClient, null, true);
    }

    public SpyMemcache(MemcachedClient memcachedClient, ConnectionFactory connectionFactory, boolean asyncWrites) {
        this.memcachedClient = memcachedClient;
        this.connectionFactory = connectionFactory;
        this.asyncWrites = asyncWrites;
        if (connectionFactory == null) {
            operationTimeout = null;
        }
        else {
            operationTimeout = connectionFactory.getOperationTimeout();
            log.debug("Using operationTimeout {}ms", operationTimeout);
        }
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
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

    public Map<String, Object> getMulti(String... keys) {
        try {
            return memcachedClient.getBulk(keys);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnGet(StringUtils.join(keys, ", "), e);
        }
        return null;
    }

    public void set(String key, int cacheTimeSeconds, Object o) {
        log.debug("MemcachedClient.set({})", key);
        try {
            OperationFuture<Boolean> future = memcachedClient.set(key, cacheTimeSeconds, o);
            waitIfNecessary(future);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnSet(key, cacheTimeSeconds, o, e);
        }
    }

    public void delete(String key) {
        try {
            OperationFuture<Boolean> future = memcachedClient.delete(key);
            waitIfNecessary(future);
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

    protected void waitIfNecessary(OperationFuture<Boolean> future) throws InterruptedException, TimeoutException, ExecutionException {
        if (!asyncWrites || operationTimeout == null) {
            future.get(operationTimeout, TimeUnit.MILLISECONDS);
        }
    }
}
