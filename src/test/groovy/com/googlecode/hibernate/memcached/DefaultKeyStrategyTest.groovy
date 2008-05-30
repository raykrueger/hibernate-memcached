package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class DefaultKeyStrategyTest extends GroovyTestCase {

    DefaultKeyStrategy strategy = new DefaultKeyStrategy()

    void test() {
        String key = strategy.toKey("test", 0, "boing")
        assertEquals("test:0:boing", key)
    }

    void test_null_region_and_null_key() {
        String key = strategy.toKey(null, 0, null)
        assertEquals("null:0:null", key)
    }

    void test_spaces() {
        String key = strategy.toKey("I have spaces", 0, "so do I")
        assertEquals("Ihavespaces:0:sodoI", key)
    }

    void test_really_long_keys_get_truncated() {
        String regionName = ""
        String key = ""
        150.times {
            regionName = key += "x"
        }

        assertEquals("f42403ec-dc00-398a-bf14-72669e131636", strategy.toKey(regionName, 0, key))
    }

}