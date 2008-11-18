package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class StringKeyStrategyTest extends AbstractKeyStrategyTestCase {

    public KeyStrategy getKeyStrategy() {
        new StringKeyStrategy()
    }

    void test() {
        assert_cache_key_equals "test:0:boing", "test", 0, "boing"
    }

    void test_config() {
        assertEquals(StringKeyStrategy.DEFAULT_MAX_KEY_LENGTH, strategy.maxKeyLength)
        strategy.maxKeyLength = 1
        assertEquals(1, strategy.maxKeyLength)
    }

    void test_null_region() {
        assert_cache_key_equals "null:0:boing", null, 0, "boing"
    }

    void test_null_key_does_not_validate() {
        assert_null_key_does_not_validate()
    }

    void test_spaces() {
        assert_cache_key_equals "Ihavespaces:0:sodoI", "I have spaces", 0, "so do I"
    }

    void test_really_long_keys_get_truncated() {
        String regionName = ""
        250.times {regionName += "x"}
        assert_cache_key_equals "fe009b44a903277f4b8e07f2cb03e96f", regionName, 0, "blah blah blah"
    }

}