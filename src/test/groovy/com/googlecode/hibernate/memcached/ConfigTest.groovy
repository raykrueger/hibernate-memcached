package com.googlecode.hibernate.memcached
/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
class ConfigTest extends BaseTestCase {

    Config newConfig(Properties properties) {
        return new Config(new PropertiesHelper(properties))
    }

    void test_cache_time_seconds() {
        Properties p = new Properties()
        p["hibernate.memcached.cacheTimeSeconds"] = "10"
        p["hibernate.memcached.REGION.cacheTimeSeconds"] = "20"

        Config config = newConfig(p)
        assertEquals 10, config.getCacheTimeSeconds(null)
        assertEquals 20, config.getCacheTimeSeconds("REGION")
    }

    void test_clear_supported() {

        Properties p = new Properties()
        p["hibernate.memcached.clearSupported"] = "true"
        p["hibernate.memcached.REGION.clearSupported"] = "false"

        Config config = newConfig(p)
        assertTrue config.isClearSupported(null)
        assertFalse config.isClearSupported("REGION")
    }

    void test_key_strategy_name() {

        Properties p = new Properties()
        p["hibernate.memcached.keyStrategy"] = "batman"
        p["hibernate.memcached.REGION.keyStrategy"] = "robin"

        Config config = newConfig(p)
        assertEquals "batman", config.getKeyStrategyName(null)
        assertEquals "robin", config.getKeyStrategyName("REGION")
    }

    void test_dogpile_prevention() {

        Properties p = new Properties()
        p["hibernate.memcached.dogpilePrevention"] = "true"
        p["hibernate.memcached.REGION.dogpilePrevention"] = "false"

        Config config = newConfig(p)
        assertTrue config.isDogpilePreventionEnabled(null)
        assertFalse config.isDogpilePreventionEnabled("REGION")
    }

    void test_dogpile_prevention_expiration_factor() {
        Properties p = new Properties()
        p["hibernate.memcached.dogpilePrevention.expirationFactor"] = "10"
        p["hibernate.memcached.REGION.dogpilePrevention.expirationFactor"] = "20"

        Config config = newConfig(p)
        assertEquals 10, config.getDogpilePreventionExpirationFactor(null)
        assertEquals 20, config.getDogpilePreventionExpirationFactor("REGION")
    }

    void test_memcache_client_factory_name() {

        Properties p = new Properties()
        Config config = newConfig(p)
        //test default
        assertEquals "com.googlecode.hibernate.memcached.spymemcached.SpyMemcacheClientFactory",
                config.getMemcachedClientFactoryName()

        p["hibernate.memcached.memcacheClientFactory"] = "blah"
        assertEquals "blah", config.getMemcachedClientFactoryName()


    }
}