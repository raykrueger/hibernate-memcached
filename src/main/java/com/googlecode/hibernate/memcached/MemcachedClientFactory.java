package com.googlecode.hibernate.memcached;

import net.spy.memcached.MemcachedClient;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public interface MemcachedClientFactory {

    MemcachedClient createMemcachedClient() throws Exception;

}
