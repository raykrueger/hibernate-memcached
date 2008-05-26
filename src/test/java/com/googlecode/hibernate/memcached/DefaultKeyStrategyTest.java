package com.googlecode.hibernate.memcached;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class DefaultKeyStrategyTest extends TestCase {

    DefaultKeyStrategy strategy = new DefaultKeyStrategy();

    public void test() {
        String key = strategy.toKey("test", 0, "boing");
        assertEquals("test:0:boing", key);
    }

    public void test_null_namespace_and_null_key() {
        String key = strategy.toKey(null, 0, null);
        assertEquals("null:0:null", key);

    }

}
