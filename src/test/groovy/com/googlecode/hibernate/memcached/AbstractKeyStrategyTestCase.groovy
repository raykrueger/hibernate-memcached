package com.googlecode.hibernate.memcached

import org.junit.Test

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
abstract class AbstractKeyStrategyTestCase extends BaseTestCase {

    protected KeyStrategy strategy

    protected void setUp() {
        strategy = getKeyStrategy()
    }

    void assert_cache_key_equals (expected, namespace, clearIndex, keyObject) {
        String key = strategy.toKey(namespace, clearIndex, keyObject)
        assertEquals(expected, key)
    }

    void test_assert_null_key_does_not_validate() {
        try {
            strategy.toKey(null, 0, null)
            fail(IllegalArgumentException.class.name + " expected.");
        } catch (IllegalArgumentException expected) {
        }
    }

    abstract KeyStrategy getKeyStrategy();

}
