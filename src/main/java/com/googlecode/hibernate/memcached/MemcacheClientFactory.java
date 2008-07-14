package com.googlecode.hibernate.memcached;

/**
 * Simple interface used to abstract the creation of the MemcachedClient
 *
 * @author Ray Krueger
 */
public interface MemcacheClientFactory {

    Memcache createMemcacheClient() throws Exception;

}
