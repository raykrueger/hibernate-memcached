package com.googlecode.hibernate.memcached;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class MemcachedProviderTest extends BaseTestCase {

    MemcachedCacheProvider provider

    void setUp() {
        provider = new MemcachedCacheProvider()
    }

    void test_defaults() {
        Properties properties = new Properties()
        provider.start(properties)
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties)
        assertNotNull(cache)

        //assert Defaults
        assertFalse(cache.isClearSupported())
        assertEquals(300, cache.getCacheTimeSeconds())
        assertEquals HashCodeKeyStrategy.class, cache.getKeyStrategy().class
    }

    void test_region_properties() {
        Properties properties = new Properties()

        properties.setProperty "hibernate.memcached.serverList", "127.0.0.1:11211"
        properties.setProperty "hibernate.memcached.test.cacheTimeSeconds", "500"
        properties.setProperty "hibernate.memcached.test.clearSupported", "true"
        properties.setProperty "hibernate.memcached.test.keyStrategy", StringKeyStrategy.class.getName()

        provider.start(properties)
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties)
        assertNotNull(cache)

        //assert Defaults
        assertTrue(cache.isClearSupported())
        assertEquals(500, cache.getCacheTimeSeconds())
        assertEquals(StringKeyStrategy.class, cache.getKeyStrategy().class)
    }

    void test_string_key_strategy() {
        Properties properties = new Properties()

        properties.setProperty("hibernate.memcached.keyStrategy", StringKeyStrategy.class.getName())

        provider.start(properties)
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties)
        assertNotNull(cache)
    }

    void tearDown() {
        provider.stop()
    }
}
