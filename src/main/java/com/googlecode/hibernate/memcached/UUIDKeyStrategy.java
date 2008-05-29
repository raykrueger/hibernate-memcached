package com.googlecode.hibernate.memcached;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

/**
 * {@link com.googlecode.hibernate.memcached.KeyStrategy} implementation that encodes all keys
 * using {@link java.util.UUID#nameUUIDFromBytes(byte[])}.
 * <p/>
 * For example...<br/>
 * <code>strategy.toKey("test", 0, "test")</code><br/>
 * The code above will <b>always</b> return <i>c0a30f2b-3803-3859-90e9-13dae6445d03</i>
 *
 * @author Ray Krueger
 */
public class UUIDKeyStrategy extends AbstractKeyStrategy {

    private static final Log log = LogFactory.getLog(UUIDKeyStrategy.class);

    public String toKey(String namespace, long namespaceIndex, Object key) {
        String keyString = convertKeyToString(namespace, namespaceIndex, key);
        String uuidString = UUID.nameUUIDFromBytes(keyString.getBytes()).toString();

        if (log.isDebugEnabled()) {
            log.debug("Converted keyString [" + keyString + "] to uuidString [" + uuidString + "]");
        }

        return uuidString;
    }
}
