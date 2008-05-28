package com.googlecode.hibernate.memcached;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class UUIDKeyStrategyTest extends BaseTestCase {

    UUIDKeyStrategy strategy = new UUIDKeyStrategy();

    public void test() throws Exception {
        String key = strategy.toKey("test", 0, "test");
        assertEquals("c0a30f2b-3803-3859-90e9-13dae6445d03", key);
    }


}
