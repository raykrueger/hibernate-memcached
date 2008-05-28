/* Copyright 2008 Ray Krueger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.hibernate.memcached;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class MemcachedProviderTest extends BaseTestCase {
    private MemcachedCacheProvider provider;

    public void test_defaults() throws Exception {
        Properties properties = new Properties();
        provider.start(properties);
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties);
        assertNotNull(cache);

        //assert Defaults
        assertFalse(cache.isClearSupported());
        assertEquals(300, cache.getCacheTimeSeconds());
    }

    public void test_region_properties() {
        MemcachedCacheProvider provider = new MemcachedCacheProvider();
        Properties properties = new Properties();

        properties.setProperty("hibernate.memcached.serverList", "127.0.0.1:11211");
        properties.setProperty("hibernate.memcached.test.cacheTimeSeconds", "500");
        properties.setProperty("hibernate.memcached.test.clearSupported", "true");

        provider.start(properties);
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties);
        assertNotNull(cache);

        //assert Defaults
        assertTrue(cache.isClearSupported());
        assertEquals(500, cache.getCacheTimeSeconds());
    }

    protected void setUp() throws Exception {
        provider = new MemcachedCacheProvider();
    }

    protected void tearDown() throws Exception {
        provider.stop();
    }
}
