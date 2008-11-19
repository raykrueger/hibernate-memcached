package com.googlecode.hibernate.memcached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ray Krueger
 */
public class LoggingMemcacheExceptionHandler implements MemcacheExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingMemcacheExceptionHandler.class);

    public void handleErrorOnGet(String key, Exception e) {
        log.warn("Cache 'get' failed for key [" + key + "]", e);
    }

    public void handleErrorOnSet(String key, int cacheTimeSeconds, Object o, Exception e) {
        log.warn("Cache 'set' failed for key [" + key + "]", e);
    }

    public void handleErrorOnDelete(String key, Exception e) {
        log.warn("Cache 'delete' failed for key [" + key + "]", e);
    }

    public void handleErrorOnIncr(String key, int factor, int startingValue, Exception e) {
        log.warn("Cache 'incr' failed for key [" + key + "]", e);
    }
}
