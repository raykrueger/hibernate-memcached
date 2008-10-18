package com.googlecode.hibernate.memcached.dangamemcached;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;
import com.googlecode.hibernate.memcached.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DOCUMENT ME!
 *
 * @author George Wei
 */
public class SimpleErrorHandler implements ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(SimpleErrorHandler.class);

    public void handleErrorOnDelete(MemCachedClient client, Throwable error,
                                    String cacheKey) {
        log.error("Error on delete cacheKey [{}]: {}", cacheKey, error);
    }

    public void handleErrorOnFlush(MemCachedClient client, Throwable error) {
        log.error("Error on flush: {}", error);
    }

    public void handleErrorOnGet(MemCachedClient client, Throwable error,
                                 String cacheKey) {
        log.error("Error on get cacheKey [{}]: {}", cacheKey, error);
    }

    public void handleErrorOnGet(MemCachedClient client, Throwable error,
                                 String[] cacheKeys) {
        handleErrorOnGet(client, error, StringUtils.join(cacheKeys, ", "));
    }

    public void handleErrorOnInit(MemCachedClient client, Throwable error) {
        log.error("Error on initialization: {}", error);
    }

    public void handleErrorOnSet(MemCachedClient client, Throwable error,
                                 String cacheKey) {
        log.error("Error on set cacheKey [{}]: {}", cacheKey, error);
    }

    public void handleErrorOnStats(MemCachedClient client, Throwable error) {
        log.error("Error on stats: {}", error);
    }
}
