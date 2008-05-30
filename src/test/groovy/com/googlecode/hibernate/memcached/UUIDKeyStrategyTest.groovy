package com.googlecode.hibernate.memcached
/**
 * @author Ray Krueger
 */
class UUIDKeyStrategyTest extends GroovyTestCase {

    void testSomeShit() {
        UUIDKeyStrategy strategy = new UUIDKeyStrategy()
        def key = strategy.toKey("test", 0, "test")
        assertEquals "c0a30f2b-3803-3859-90e9-13dae6445d03", key
    }

}