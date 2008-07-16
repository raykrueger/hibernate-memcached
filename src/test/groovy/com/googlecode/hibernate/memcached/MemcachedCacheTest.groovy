package com.googlecode.hibernate.memcached
/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
class MemcachedCacheTest extends BaseTestCase {

    MemcachedCache cache

    void test_basics() {
        cache = new MemcachedCache("region", new MockMemcached())
        assertNull cache.get("test")

        cache.put "test", "value"
        assertEquals "value", cache.get("test")

        cache.update "test", "blah"
        assertEquals "blah", cache.read("test")

        cache.remove "test"
        assertNull cache.get("test")

    }

    void test_dogpile_cache_miss() {
        MockMemcached mockCache = new MockMemcached()
        cache = new MemcachedCache("region", mockCache)
        cache.dogpilePreventionEnabled = true
        cache.cacheTimeSeconds = 1
        cache.dogpilePreventionExpirationFactor = 2

        assertNull cache.get("test")
        assertEquals MemcachedCache.DOGPILE_TOKEN, mockCache.cache["region:0:3556498.dogpileTokenKey"]

        cache.put("test", "value")
        assertEquals "value", mockCache.cache["region:0:3556498"]
    }

    void test_dogpile_cache_hit() {
        MockMemcached mockCache = new MockMemcached()
        cache = new MemcachedCache("region", mockCache)
        cache.dogpilePreventionEnabled = true
        cache.cacheTimeSeconds = 1
        cache.dogpilePreventionExpirationFactor = 2

        cache.put("test", "value")
        assertEquals "value", mockCache.cache["region:0:3556498"]
        assertEquals MemcachedCache.DOGPILE_TOKEN, mockCache.cache["region:0:3556498.dogpileTokenKey"]
    }

}