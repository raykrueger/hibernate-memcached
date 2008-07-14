package com.googlecode.hibernate.memcached.spymemcached

import com.googlecode.hibernate.memcached.BaseTestCase
import com.googlecode.hibernate.memcached.Memcache
import com.googlecode.hibernate.memcached.PropertiesHelper
import com.googlecode.hibernate.memcached.spymemcached.SpyMemcacheClientFactory
import net.spy.memcached.HashAlgorithm


class SpyMemcacheClientFactoryTest extends BaseTestCase {

    Memcache client

    void test_defaults() {
        SpyMemcacheClientFactory factory = new SpyMemcacheClientFactory(new PropertiesHelper(new Properties()))
        client = factory.createMemcacheClient()
        assert client
    }

    void test_all_properties_set() {
        Properties properties = new Properties()

        properties.setProperty "hibernate.memcached.servers", "localhost:11211 localhost:11212"
        properties.setProperty "hibernate.memcached.hashAlgorithm", HashAlgorithm.CRC32_HASH.name()
        properties.setProperty "hibernate.memcached.operationQueueLength", "8192"
        properties.setProperty "hibernate.memcached.readBufferLength", "8192"
        properties.setProperty "hibernate.memcached.operationTimeout", "5000"

        SpyMemcacheClientFactory factory = new SpyMemcacheClientFactory(new PropertiesHelper(new Properties()))
        client = factory.createMemcacheClient()
        assert client
    }

    protected void tearDown() {
        if (client) client.shutdown()
    }

}