package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class HashCodeKeyStrategyTest extends AbstractKeyStrategyTestCase {

    public KeyStrategy getKeyStrategy() {
        return new HashCodeKeyStrategy()
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

    void test_really_long_keys_get_truncated() {
        String regionName = ""
        250.times {regionName += "x"}
        assert_cache_key_equals "e2e82011e3d56dd6be564fdcb72a8d64", regionName, 0, "blah blah blah"
    }

}