package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class HashCodeKeyStrategyTest extends BaseTestCase {

    HashCodeKeyStrategy strategy = new HashCodeKeyStrategy()

    void test() {
        String key = strategy.toKey("test", 0, "boing")
        assertEquals("test:0:93916277", key)
    }

    void test_null_region() {
        String key = strategy.toKey(null, 0, "boing")
        assertEquals("null:0:93916277", key)
    }

    void test_null_key_does_not_validate() {
        shouldFailWithCause(IllegalArgumentException.class) {
            strategy.toKey(null, 0, null)
        }
    }

    void test_spaces() {
        String key = strategy.toKey("I have spaces", 0, "so do I")
        assertEquals("Ihavespaces:0:-2100783816", key)
    }

    void test_really_long_keys_get_truncated() {
        String regionName = ""

        250.times {regionName += "x"}

        assertEquals("e2e82011-e3d5-3dd6-be56-4fdcb72a8d64", strategy.toKey(regionName, 0, "blah blah blah"))
    }

}