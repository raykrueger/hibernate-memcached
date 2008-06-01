package com.googlecode.hibernate.memcached;

import net.spy.memcached.MemcachedClient;

/**
 * Simple interface used to abstract the creation of the MemcachedClient
 *
 * @author Ray Krueger
 */
public interface MemcachedClientFactory {

    MemcachedClient createMemcachedClient() throws Exception;

}
