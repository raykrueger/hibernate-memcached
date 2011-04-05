package com.googlecode.hibernate.memcached

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

    void assert_null_key_does_not_validate() {
        shouldFailWithCause(IllegalArgumentException.class) {
            strategy.toKey(null, 0, null)
        }
    }

    abstract KeyStrategy getKeyStrategy();

}
