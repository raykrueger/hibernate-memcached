package com.googlecode.hibernate.memcached;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class MemcachedProviderTest extends BaseTestCase {

    MemcachedCacheProvider provider

    void setUp() {
        //LoggingConfig.initializeLogging()
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
    }

    void test_region_properties() {
        MemcachedCacheProvider provider = new MemcachedCacheProvider()
        Properties properties = new Properties()

        properties.setProperty("hibernate.memcached.serverList", "127.0.0.1:11211")
        properties.setProperty("hibernate.memcached.test.cacheTimeSeconds", "500")
        properties.setProperty("hibernate.memcached.test.clearSupported", "true")

        provider.start(properties)
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties)
        assertNotNull(cache)

        //assert Defaults
        assertTrue(cache.isClearSupported())
        assertEquals(500, cache.getCacheTimeSeconds())
    }

    void test_uuid_key_strategy() {
        MemcachedCacheProvider provider = new MemcachedCacheProvider()
        Properties properties = new Properties()

        properties.setProperty("hibernate.memcached.keyStrategy", UUIDKeyStrategy.class.getName())

        provider.start(properties)
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties)
        assertNotNull(cache)
    }

    void tearDown() {
        provider.stop()
    }
}
