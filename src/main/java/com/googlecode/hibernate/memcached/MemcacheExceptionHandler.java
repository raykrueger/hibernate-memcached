package com.googlecode.hibernate.memcached;

/**
 * @author Ray Krueger
 */
public interface MemcacheExceptionHandler {

    void handleErrorOnGet(String key, Exception e);

    void handleErrorOnSet(String key, int cacheTimeSeconds, Object o, Exception e);

    void handleErrorOnDelete(String key, Exception e);

    void handleErrorOnIncr(String key, int factor, int startingValue, Exception e);

}
