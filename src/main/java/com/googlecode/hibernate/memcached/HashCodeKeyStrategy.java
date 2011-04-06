package com.googlecode.hibernate.memcached;

/**
 * Transforms the key object using key.hashCode()
 *
 * @deprecated as of 1.3 HashCodeKeyStrategy is deprecated and Sha1KeyStrategy is the default. Use that instead.
 * @author Ray Krueger
 */
@Deprecated
public class HashCodeKeyStrategy extends AbstractKeyStrategy {

    protected String transformKeyObject(Object key) {
        int hashCode = key.hashCode();
        log.debug("Transformed key [{}] to hashCode [{}]", key, hashCode);
        return String.valueOf(hashCode);
    }

}
