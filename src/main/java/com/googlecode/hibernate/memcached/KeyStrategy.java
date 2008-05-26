package com.googlecode.hibernate.memcached;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public interface KeyStrategy {

    String toKey(String namespace, long namespaceIndex, Object key);
}
