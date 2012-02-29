package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class HashCodeKeyStrategyTest extends AbstractKeyStrategyTestCase {

    KeyStrategy getKeyStrategy() {
        new HashCodeKeyStrategy()
    }

    void test() {
        assert_cache_key_equals "test:0:93916277", "test", 0, "boing"
    }

    void test_null_region() {
        assert_cache_key_equals "null:0:93916277", null, 0, "boing"
    }

    void test_null_key_does_not_validate() {
        assert_null_key_does_not_validate()
    }

    void test_spaces() {
        assert_cache_key_equals "Ihavespaces:0:-2100783816", "I have spaces", 0, "so do I"
    }

    void test_really_long_key_throws_exception() {
        StringBuilder regionName = new StringBuilder()
        250.times {regionName << "x"}
        shouldFail(IllegalArgumentException) {
            getKeyStrategy().toKey(regionName.toString(), 0, "blah blah blah")
        }
    }
}
