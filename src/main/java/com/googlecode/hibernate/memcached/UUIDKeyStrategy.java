package com.googlecode.hibernate.memcached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger log = LoggerFactory.getLogger(UUIDKeyStrategy.class);

    public String toKey(String regionName, long clearIndex, Object key) {
        String keyString = convertKeyToString(regionName, clearIndex, key);
        String uuidString = UUID.nameUUIDFromBytes(keyString.getBytes()).toString();

        log.debug("Converted keyString [{}] to uuidString [{}]", keyString, uuidString);

        return uuidString;
    }
}
