package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class Sha1KeyStrategyTest extends AbstractKeyStrategyTestCase {

    KeyStrategy getKeyStrategy() {
        new Sha1KeyStrategy()
    }

    void test() {
        assert_cache_key_equals "cd23e26dd7ab1d052e1c0a04daa27a03f6cd5d1c", "test", 0, "boing"
    }

    void test_null_region() {
        assert_cache_key_equals "6afcec5614479d46a1ec6d73dabbc2cea154da3c", null, 0, "boing"
    }

    void test_null_key_does_not_validate() {
        assert_null_key_does_not_validate()
    }

    void test_spaces() {
        assert_cache_key_equals "949b2a6fce917d85bd56e6197c93b3affa694e50", "I have spaces", 0, "so do I"
    }

    void test_really_long_keys_get_truncated() {
        StringBuilder regionName = new StringBuilder()
        250.times {regionName << "x"}
        assert_cache_key_equals "7f00c6faf1fefaf62cabb512285cc60ce641d5c8", regionName.toString(), 0, "blah blah blah"
    }
}
