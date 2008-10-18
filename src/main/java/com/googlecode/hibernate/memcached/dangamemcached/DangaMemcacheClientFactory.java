package com.googlecode.hibernate.memcached.dangamemcached;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcacheClientFactory;
import com.googlecode.hibernate.memcached.PropertiesHelper;
import org.hibernate.cache.CacheException;

/**
 * DOCUMENT ME!
 *
 * @author George Wei
 */
public class DangaMemcacheClientFactory implements MemcacheClientFactory {

    public static final String PROP_PREFIX = "hibernate.memcached.";

    public static final String PROP_COMPRESS_ENABLE = PROP_PREFIX + "compressEnable";
    public static final String PROP_DEFAULT_ENCODING = PROP_PREFIX + "defaultEncoding";
    public static final String PROP_POOL_NAME = PROP_PREFIX + "poolName";
    public static final String PROP_ERROR_HANDLER = PROP_PREFIX + "errorHandler";
    public static final String PROP_SERVERS = PROP_PREFIX + "servers";
    public static final String PROP_WEIGHTS = PROP_PREFIX + "weights";
    public static final String PROP_INIT_CONN = PROP_PREFIX + "initConn";
    public static final String PROP_MIN_CONN = PROP_PREFIX + "minConn";
    public static final String PROP_MAX_CONN = PROP_PREFIX + "maxConn";
    public static final String PROP_MAX_IDLE = PROP_PREFIX + "maxIdle";
    public static final String PROP_MAINT_SLEEP = PROP_PREFIX + "maintSleep";
    public static final String PROP_SOCKET_TIMEOUT = PROP_PREFIX + "socketTimeout";
    public static final String PROP_SOCKET_CONNECT_TIMEOUT = PROP_PREFIX + "socketConnectTimeout";

    public static final boolean DEFAULT_COMPRESS_ENABLE = true;
    public static final String DEFAULT_DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_POOL_NAME = "default";
    public static final String DEFAULT_ERROR_HANDLER = "com.googlecode.hibernate.memcached.dangamemcached.SimpleErrorHandler";
    public static final String DEFAULT_SERVERS = "localhost:11211";
    public static final int DEFAULT_INIT_CONN = 1;
    public static final int DEFAULT_MIN_CONN = 1;
    public static final int DEFAULT_MAX_CONN = 10;
    public static final int DEFAULT_MAX_IDLE = 1000 * 60 * 5; //5 minutes
    public static final int DEFAULT_MAINT_SLEEP = 1000 * 30;     //30 seconds
    public static final int DEFAULT_SOCKET_TIMEOUT = 1000 * 30;     //30 seconds
    public static final int DEFAULT_SOCKET_CONNECT_TIMEOUT = 1000 * 3;      //3 seconds

    private PropertiesHelper properties;

    public DangaMemcacheClientFactory(PropertiesHelper properties) {
        this.properties = properties;
    }

    public Memcache createMemcacheClient() throws Exception {
        String poolName = getPoolName();

        // grab an instance of our connection pool
        SockIOPool pool = SockIOPool.getInstance(poolName);

        // set the servers and the weights
        pool.setServers(getServers());
        pool.setWeights(getWeights());

        // set some basic pool settings
        pool.setInitConn(getInitConn());
        pool.setMinConn(getMinConn());
        pool.setMaxConn(getMaxConn());
        pool.setMaxIdle(getMaxIdle());

        // set the sleep for the maint thread
        // it will wake up every x seconds and
        // maintain the pool size
        pool.setMaintSleep(getMaintSleep());

        // set some TCP settings
        pool.setNagle(false);
        pool.setSocketTO(getSocketTimeout());
        pool.setSocketConnectTO(getSocketConnectTimeout());

        // initialize the connection pool
        pool.initialize();

        MemCachedClient client =
                new MemCachedClient(
                        getClassLoader(),
                        getErrorHandler(),
                        poolName);
        client.setCompressEnable(isCompressEnable());
        client.setDefaultEncoding(getDefaultEncoding());

        return new DangaMemcache(client, poolName);
    }

    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public boolean isCompressEnable() {
        return properties.getBoolean(PROP_COMPRESS_ENABLE, DEFAULT_COMPRESS_ENABLE);
    }

    public String getDefaultEncoding() {
        return properties.get(PROP_DEFAULT_ENCODING, DEFAULT_DEFAULT_ENCODING);
    }

    public String getPoolName() {
        return properties.get(PROP_POOL_NAME, DEFAULT_POOL_NAME);
    }

    public ErrorHandler getErrorHandler() {
        String errorHandlerName =
                properties.get(PROP_ERROR_HANDLER, DEFAULT_ERROR_HANDLER);

        ErrorHandler errorHandler;
        try {
            errorHandler =
                    (ErrorHandler) Class.forName(errorHandlerName).newInstance();
        } catch (ClassNotFoundException e) {
            throw new CacheException(
                    "Unable to find error handler class [" + errorHandlerName + "]");
        } catch (IllegalAccessException e) {
            throw new CacheException(
                    "Illegally accessed error handler class [" + errorHandlerName + "]");
        } catch (InstantiationException e) {
            throw new CacheException(
                    "Failed to instantiate error handler class [" + errorHandlerName + "]");
        }

        return errorHandler;
    }

    public String[] getServers() {
        return properties.get(PROP_SERVERS, DEFAULT_SERVERS).split(" ");
    }

    public Integer[] getWeights() {
        String[] servers = getServers();
        Integer[] weights = new Integer[servers.length];
        String weightsValue = properties.get(PROP_WEIGHTS);

        if (weightsValue == null || "".equals(weightsValue)) {
            for (int i = 0; i < weights.length; i++)
                weights[i] = 1;
        } else {
            String[] weightsStrings = weightsValue.split(" ");
            if (weightsStrings.length == servers.length) {
                for (int i = 0; i < weights.length; i++)
                    weights[i] = new Integer(weightsStrings[i]);
            } else
                throw new CacheException(
                        "Count of weight number mismatch count of server");
        }

        return weights;
    }

    public int getInitConn() {
        return properties.getInt(PROP_INIT_CONN, DEFAULT_INIT_CONN);
    }

    public int getMinConn() {
        return properties.getInt(PROP_MIN_CONN, DEFAULT_MIN_CONN);
    }

    public int getMaxConn() {
        return properties.getInt(PROP_MAX_CONN, DEFAULT_MAX_CONN);
    }

    public int getMaxIdle() {
        return properties.getInt(PROP_MAX_IDLE, DEFAULT_MAX_IDLE);
    }

    public int getMaintSleep() {
        return properties.getInt(PROP_MAINT_SLEEP, DEFAULT_MAINT_SLEEP);
    }

    public int getSocketTimeout() {
        return properties.getInt(PROP_SOCKET_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    public int getSocketConnectTimeout() {
        return properties.getInt(PROP_SOCKET_CONNECT_TIMEOUT, DEFAULT_SOCKET_CONNECT_TIMEOUT);
    }
}
