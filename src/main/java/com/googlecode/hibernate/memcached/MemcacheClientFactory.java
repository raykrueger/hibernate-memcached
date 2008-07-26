package com.googlecode.hibernate.memcached;

/**
 * Simple interface used to abstract the creation of the MemcachedClient
 * All implementers must have a constructor that takes an instance of
 * {@link com.googlecode.hibernate.memcached.PropertiesHelper}.
 *
 * @author Ray Krueger
 */
public interface MemcacheClientFactory {

    Memcache createMemcacheClient() throws Exception;

}
