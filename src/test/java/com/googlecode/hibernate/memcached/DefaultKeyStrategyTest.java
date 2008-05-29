package com.googlecode.hibernate.memcached;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class DefaultKeyStrategyTest extends BaseTestCase {

    DefaultKeyStrategy strategy = new DefaultKeyStrategy();

    public void test() {
        String key = strategy.toKey("test", 0, "boing");
        assertEquals("test:0:boing", key);
    }

    public void test_null_region_and_null_key() {
        String key = strategy.toKey(null, 0, null);
        assertEquals("null:0:null", key);
    }

    public void test_spaces() throws Exception {
        String key = strategy.toKey("I have spaces", 0, "so do I");
        assertEquals("Ihavespaces:0:sodoI", key);
    }

    public void test_really_long_keys_get_truncated() throws Exception {
        String regionName = "";
        String key = "";
        for (int i = 0; i < 150; i++) {
            key += "x";
            regionName += "x";
        }

        assertEquals("f42403ec-dc00-398a-bf14-72669e131636", strategy.toKey(regionName, 0, key));
    }

    public void test_custom_truncation() throws Exception {

        strategy = new DefaultKeyStrategy() {
            protected String truncateKey(String key) {
                return "great-googly-moogly";
            }
        };

        String regionName = "";
        String key = "";
        for (int i = 0; i < 150; i++) {
            key += "x";
            regionName += "x";
        }

        assertEquals("great-googly-moogly", strategy.toKey(regionName, 0, key));
    }

}
