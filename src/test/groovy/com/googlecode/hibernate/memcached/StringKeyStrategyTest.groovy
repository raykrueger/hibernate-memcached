package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class StringKeyStrategyTest extends BaseTestCase {

    StringKeyStrategy strategy

    protected void setUp() {
        strategy = new StringKeyStrategy()
    }

    void test() {
        String key = strategy.toKey("test", 0, "boing")
        assertEquals("test:0:boing", key)
    }

    void test_config() {
        assertEquals(StringKeyStrategy.DEFAULT_MAX_KEY_LENGTH, strategy.maxKeyLength)
        strategy.maxKeyLength = 1
        assertEquals(1, strategy.maxKeyLength)
    }

    void test_null_region() {
        String key = strategy.toKey(null, 0, "boing")
        assertEquals("null:0:boing", key)
    }

    void test_null_key_does_not_validate() {
        shouldFailWithCause(IllegalArgumentException.class) {
            strategy.toKey(null, 0, null)
        }
    }

    void test_spaces() {
        String key = strategy.toKey("I have spaces", 0, "so do I")
        assertEquals("Ihavespaces:0:sodoI", key)
    }

    void test_really_long_keys_get_truncated() {
        String regionName = ""

        250.times {regionName += "x"}

        assertEquals("fe009b44a903277f4b8e07f2cb03e96f", strategy.toKey(regionName, 0, "blah blah blah"))
    }

}