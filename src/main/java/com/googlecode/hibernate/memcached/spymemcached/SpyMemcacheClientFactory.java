package com.googlecode.hibernate.memcached.spymemcached;

import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcacheClientFactory;
import com.googlecode.hibernate.memcached.PropertiesHelper;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;

/**
 * Parses hibernate properties to produce a MemcachedClient.<br/>
 * See {@link com.googlecode.hibernate.memcached.MemcachedCacheProvider} for property details.
 *
 * @author Ray Krueger
 */
public class SpyMemcacheClientFactory implements MemcacheClientFactory {

    private static final String PROP_PREFIX = "hibernate.memcached.";
    public static final String PROP_SERVERS = PROP_PREFIX + "servers";
    public static final String PROP_OPERATION_QUEUE_LENGTH = PROP_PREFIX + "operationQueueLength";
    public static final String PROP_READ_BUFFER_SIZE = PROP_PREFIX + "readBufferSize";
    public static final String PROP_OPERATION_TIMEOUT = PROP_PREFIX + "operationTimeout";
    public static final String PROP_HASH_ALGORITHM = PROP_PREFIX + "hashAlgorithm";
    private final PropertiesHelper properties;

    public SpyMemcacheClientFactory(PropertiesHelper properties) {
        this.properties = properties;
    }

    public Memcache createMemcacheClient() throws Exception {

        DefaultConnectionFactory defaultConnectionFactory =
                new DefaultConnectionFactory(
                        getOperationQueueLength(),
                        getReadBufferSize(),
                        getHashAlgorithm()) {

                    public long getOperationTimeout() {
                        return getOperationTimeoutMillis();
                    }
                };

        MemcachedClient client = new MemcachedClient(defaultConnectionFactory, AddrUtil.getAddresses(getServerList()));
        return new SpyMemcache(client);
    }

    public String getServerList() {
        return properties.get(PROP_SERVERS, "localhost:11211");
    }

    public int getOperationQueueLength() {
        return properties.getInt(PROP_OPERATION_QUEUE_LENGTH,
                DefaultConnectionFactory.DEFAULT_OP_QUEUE_LEN);
    }

    public int getReadBufferSize() {
        return properties.getInt(PROP_READ_BUFFER_SIZE,
                DefaultConnectionFactory.DEFAULT_READ_BUFFER_SIZE);
    }

    public long getOperationTimeoutMillis() {
        return properties.getLong(PROP_OPERATION_TIMEOUT,
                DefaultConnectionFactory.DEFAULT_OPERATION_TIMEOUT);
    }

    public HashAlgorithm getHashAlgorithm() {
        return properties.getEnum(PROP_HASH_ALGORITHM,
                HashAlgorithm.class,
                HashAlgorithm.KETAMA_HASH);
    }
}
