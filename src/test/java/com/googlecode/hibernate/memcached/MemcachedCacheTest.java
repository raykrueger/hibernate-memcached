package com.googlecode.hibernate.memcached;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class MemcachedCacheTest extends BaseTestCase {

    MemcachedCache cache;
    MemcachedClient client;

    protected void setUp() throws Exception {
        client = new MemcachedClient(AddrUtil.getAddresses("localhost:11211"));
        cache = new MemcachedCache("MemcachedCacheTest", client);
    }

    protected void tearDown() throws Exception {
        client.shutdown();
    }

    public void test() throws Exception {
        cache.put("test", "value");
        Thread.sleep(500);
        assertEquals("value", cache.get("test"));
    }

    public void test_clear() throws Exception {
        cache.setClearSupported(true);
        cache.put("test", "value");
        Thread.sleep(500);
        assertEquals("value", cache.get("test"));
        cache.clear();
        Thread.sleep(500);
        assertNull(cache.get("test"));
    }

}
