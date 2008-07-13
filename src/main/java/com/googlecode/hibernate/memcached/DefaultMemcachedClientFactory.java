package com.googlecode.hibernate.memcached;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;

import java.util.Properties;

/**
 * Parses hibernate properties to produce a MemcachedClient.<br/>
 * See {@link MemcachedCacheProvider} for property details.
 *
 * @author Ray Krueger
 */
public class DefaultMemcachedClientFactory implements MemcachedClientFactory {

    private static final String PROP_PREFIX = "hibernate.memcached.";
    public static final String PROP_SERVERS = PROP_PREFIX + "servers";
    public static final String PROP_OPERATION_QUEUE_LENGTH = PROP_PREFIX + "operationQueueLength";
    public static final String PROP_READ_BUFFER_SIZE = PROP_PREFIX + "readBufferSize";
    public static final String PROP_OPERATION_TIMEOUT = PROP_PREFIX + "operationTimeout";
    public static final String PROP_HASH_ALGORITHM = PROP_PREFIX + "hashAlgorithm";
    private final PropertiesHelper properties;

    public DefaultMemcachedClientFactory(Properties properties) {
        this.properties = new PropertiesHelper(properties);
    }

    public MemcachedClient createMemcachedClient() throws Exception {

        DefaultConnectionFactory defaultConnectionFactory =
                new DefaultConnectionFactory(
                        getOperationQueueLength(),
                        getReadBufferSize(),
                        getHashAlgorithm()) {

                    public long getOperationTimeout() {
                        return getOperationTimeoutMillis();
                    }
                };

        return new MemcachedClient(defaultConnectionFactory, AddrUtil.getAddresses(getServerList()));
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
