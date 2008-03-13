package com.googlecode.hibernate.memcached;

import junit.framework.TestCase;

import java.util.Properties;

import org.hibernate.cache.Cache;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class MemcachedProviderTest extends TestCase {

    public void test() throws Exception {
        MemcachedCacheProvider provider = new MemcachedCacheProvider();
        Properties properties = new Properties();
        //properties.setProperty("memcached.servers", "");
        provider.start(properties);
        Cache cache = provider.buildCache("test", properties);
        assertNotNull(cache);
        provider.stop();
    }

}
