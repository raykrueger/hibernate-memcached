package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class Md5KeyStrategyTest extends AbstractKeyStrategyTestCase {

    public KeyStrategy getKeyStrategy() {
        return new Md5KeyStrategy()
    }

    void test() {
        assert_cache_key_equals "dfbb1717f813ecccac747d5076e2a6d5", "test", 0, "boing"
    }

    void test_null_region() {
        assert_cache_key_equals "71b3dae5a0a8d765658a6c27bed071fd", null, 0, "boing"
    }

    void test_null_key_does_not_validate() {
        assert_null_key_does_not_validate()
    }

    void test_spaces() {
        assert_cache_key_equals "23c5e5682b9a9fad5b30a95fae4ff299", "I have spaces", 0, "so do I"
    }

    void test_really_long_keys_get_truncated() {
        String regionName = ""
        250.times {regionName += "x"}
        assert_cache_key_equals "e2e82011e3d56dd6be564fdcb72a8d64", regionName, 0, "blah blah blah"
    }

}