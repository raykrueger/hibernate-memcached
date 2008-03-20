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

import junit.framework.TestCase;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class MemcachedProviderTest extends TestCase {

    public void test() throws Exception {
        MemcachedCacheProvider provider = new MemcachedCacheProvider();
        Properties properties = new Properties();
        //properties.setProperty("memcached.servers", "");
        provider.start(properties);
        MemcachedCache cache = (MemcachedCache) provider.buildCache("test", properties);
        assertNotNull(cache);

        //assert Defaults
        assertFalse(cache.isClearSupported());
        assertEquals(500, cache.getAsynchGetTimeoutMillis());
        assertEquals(300, cache.getCacheTimeSeconds());

        provider.stop();
    }

}
