package com.googlecode.hibernate.memcached

import net.spy.memcached.HashAlgorithm
import net.spy.memcached.MemcachedClient


class DefaultMemcachedClientFactoryTest extends BaseTestCase {

    MemcachedClient client

    void test_defaults() {
        DefaultMemcachedClientFactory factory = new DefaultMemcachedClientFactory(new Properties())
        client = factory.createMemcachedClient()
        assert client
    }

    void test_all_properties_set() {
        Properties properties = new Properties()

        properties.setProperty "hibernate.memcached.servers", "localhost:11211 localhost:11212"
        properties.setProperty "hibernate.memcached.hashAlgorithm", HashAlgorithm.CRC32_HASH.name()
        properties.setProperty "hibernate.memcached.operationQueueLength", "8192"
        properties.setProperty "hibernate.memcached.readBufferLength", "8192"
        properties.setProperty "hibernate.memcached.operationTimeout", "5000"

        DefaultMemcachedClientFactory factory = new DefaultMemcachedClientFactory(properties)
        client = factory.createMemcachedClient()
        assert client
    }

    protected void tearDown() {
        if (client) client.shutdown()
    }

}