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
    private final Properties properties;

    public DefaultMemcachedClientFactory(Properties properties) {
        this.properties = properties;
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
        return properties.getProperty(PROP_SERVERS, "localhost:11211");
    }

    private int getPropertyAsInt(String property, int defaultValue) {
        String val = properties.getProperty(property);
        if (val != null) {
            return Integer.parseInt(val);
        }
        return defaultValue;
    }

    public int getOperationQueueLength() {
        return getPropertyAsInt(PROP_OPERATION_QUEUE_LENGTH, DefaultConnectionFactory.DEFAULT_OP_QUEUE_LEN);
    }

    public int getReadBufferSize() {
        return getPropertyAsInt(PROP_READ_BUFFER_SIZE, DefaultConnectionFactory.DEFAULT_READ_BUFFER_SIZE);
    }

    public long getOperationTimeoutMillis() {
        String val = properties.getProperty(PROP_OPERATION_TIMEOUT);
        if (val != null) {
            return Long.parseLong(val);
        }

        return DefaultConnectionFactory.DEFAULT_OPERATION_TIMEOUT;
    }

    public HashAlgorithm getHashAlgorithm() {
        String val = properties.getProperty(PROP_HASH_ALGORITHM);
        if (val != null) {
            return HashAlgorithm.valueOf(val);
        }
        return HashAlgorithm.KETAMA_HASH;
    }
}
