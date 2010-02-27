package com.googlecode.hibernate.memcached;

/**
 * Transforms the key object using key.hashCode()
 *
 * @author Ray Krueger
 */
public class HashCodeKeyStrategy extends AbstractKeyStrategy {

    protected String transformKeyObject(Object key) {
        int hashCode = key.hashCode();
        log.debug("Transformed key [{}] to hashCode [{}]", key, hashCode);
        return String.valueOf(hashCode);
    }

}
