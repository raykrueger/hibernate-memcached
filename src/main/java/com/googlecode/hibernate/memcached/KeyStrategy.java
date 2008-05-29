package com.googlecode.hibernate.memcached;

/**
 * Strategy interface for parsing the parts used by {@link MemcachedCache} to generate cache keys.
 *
 * @author Ray Krueger
 */
public interface KeyStrategy {

    String toKey(String regionName, long namespaceIndex, Object key);
}
