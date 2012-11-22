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
        assert_cache_key_equals "a088ce3b48a12c8a8f26058240f4518d", "test", 0, "boing"
    }

    void test_null_region() {
        assert_cache_key_equals "cf23c7bb0c99979d4be1129adc959e6f", null, 0, "boing"
    }

    void test_spaces() {
        assert_cache_key_equals "0564810c2fd4e86dc6f355ad99e7d01b", "I have spaces", 0, "so do I"
    }

    void test_really_long_keys_get_truncated() {
        String regionName = ""
        250.times {regionName += "x"}
        assert_cache_key_equals "16df3d87c2f8bde43fcdbb545be10626", regionName, 0, "blah blah blah"
    }

}
