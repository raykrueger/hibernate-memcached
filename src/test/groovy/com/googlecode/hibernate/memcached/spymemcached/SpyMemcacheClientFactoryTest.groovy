package com.googlecode.hibernate.memcached.spymemcached

import com.googlecode.hibernate.memcached.BaseTestCase
import com.googlecode.hibernate.memcached.Memcache
import com.googlecode.hibernate.memcached.PropertiesHelper
import net.spy.memcached.DefaultHashAlgorithm
import net.spy.memcached.MemcachedClient
import net.spy.memcached.auth.PlainCallbackHandler

class SpyMemcacheClientFactoryTest extends BaseTestCase {

    private Properties properties = new Properties()
    private Memcache client
    private SpyMemcacheClientFactory factory = new SpyMemcacheClientFactory(new PropertiesHelper(properties))

    void test_defaults() {
        client = factory.createMemcacheClient()
        assert client
    }

    void test_all_properties_set() {

        properties.setProperty "hibernate.memcached.servers", "localhost:11211 localhost:11212"
        properties.setProperty "hibernate.memcached.hashAlgorithm", DefaultHashAlgorithm.CRC_HASH.name()
        properties.setProperty "hibernate.memcached.operationQueueLength", "8192"
        properties.setProperty "hibernate.memcached.readBufferLength", "8192"
        properties.setProperty "hibernate.memcached.operationTimeout", "5000"
        properties.setProperty "hibernate.memcached.daemonMode", "true"

        client = factory.createMemcacheClient()
        assert client
    }

    void testNoAuth() {
        client = factory.createMemcacheClient()
        assertTrue client instanceof SpyMemcache
        SpyMemcache spyMemcache = client
        MemcachedClient memcachedClient = spyMemcache.memcachedClient
        assertNull memcachedClient.authDescriptor
    }

    void testAuth() {
        String username = 'user'
        String password = 'pass'

        properties.setProperty 'hibernate.memcached.username', username
        properties.setProperty 'hibernate.memcached.password', password

        client = factory.createMemcacheClient()
        assertTrue client instanceof SpyMemcache

        SpyMemcache spyMemcache = client
        MemcachedClient memcachedClient = spyMemcache.memcachedClient
        assertNotNull memcachedClient.authDescriptor

        assertTrue memcachedClient.authDescriptor.cbh instanceof PlainCallbackHandler
        PlainCallbackHandler cbh = memcachedClient.authDescriptor.cbh
        assertEquals username, cbh.username
        assertEquals password.toCharArray(), cbh.password
    }

    protected void tearDown() {
        client?.shutdown()
    }
}
