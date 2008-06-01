package com.googlecode.hibernate.memcached;

/**
 * Transforms the key object using key.hashCode()
 *
 * @author Ray Krueger
 */
public class HashCodeKeyStrategy extends AbstractKeyStrategy {

    protected Object transformKeyObject(Object key) {
        int hashCode = key.hashCode();
        log.debug("Transformed key [{}] to hashCode [{}]", key, key.hashCode());
        return hashCode;
    }

}
