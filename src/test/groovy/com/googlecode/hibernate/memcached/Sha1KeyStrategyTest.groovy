package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class Sha1KeyStrategyTest extends AbstractKeyStrategyTestCase {

    public KeyStrategy getKeyStrategy() {
        return new Sha1KeyStrategy()
    }

    void test() {
        assert_cache_key_equals "5c2adf57badcd5d923228b96dd1aee3bf0d5bf2c", "test", 0, "boing"
    }

    void test_null_region() {
        assert_cache_key_equals "a0e96499b9522edc2807f4189e1cfdd65a4dad0d", null, 0, "boing"
    }

    void test_null_key_does_not_validate() {
        assert_null_key_does_not_validate()
    }

    void test_spaces() {
        assert_cache_key_equals "3344a9dadb9f405a39924d593592be1bf400e978", "I have spaces", 0, "so do I"
    }

    void test_really_long_keys_get_truncated() {
        String regionName = ""
        250.times {regionName += "x"}
        assert_cache_key_equals "3c64cd962343bc26ea73c78ba59eeed88491f439", regionName, 0, "blah blah blah"
    }

}