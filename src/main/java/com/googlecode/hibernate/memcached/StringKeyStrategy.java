package com.googlecode.hibernate.memcached;

/**
 * Simply transforms the key object using String.valueOf()
 *
 * @deprecated As of 1.3 use the Sha1KeyStrategy instead
 * @author Ray Krueger
 */
@Deprecated
public class StringKeyStrategy extends AbstractKeyStrategy {

    protected String transformKeyObject(Object key) {
        String stringKey = String.valueOf(key);
        log.debug("Transformed key [{}] to string [{}]", key, stringKey);
        return stringKey;
    }

}
