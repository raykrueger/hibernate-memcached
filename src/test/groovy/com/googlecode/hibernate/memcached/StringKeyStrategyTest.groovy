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

    void test_null_region() {
        assert_cache_key_equals "null:0:boing", null, 0, "boing"
    }

    void test_null_key_does_not_validate() {
        assert_null_key_does_not_validate()
    }

    void test_spaces() {
        assert_cache_key_equals "Ihavespaces:0:sodoI", "I have spaces", 0, "so do I"
    }

    void test_really_long_key_throws_exception() {
        String regionName = ""
        250.times {regionName += "x"}
        shouldFail(IllegalArgumentException) {
          getKeyStrategy().toKey(regionName, 0, "blah blah blah")
        }
    }

}
