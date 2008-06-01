package com.googlecode.hibernate.memcached;

/**
 * Simply transforms the key object using String.valueOf()
 *
 * @author Ray Krueger
 */
public class StringKeyStrategy extends AbstractKeyStrategy {

    protected Object transformKeyObject(Object key) {
        String stringKey = String.valueOf(key);
        log.debug("Transformed key [{}] to string [{}]", key, stringKey);
        return stringKey;
    }

}
