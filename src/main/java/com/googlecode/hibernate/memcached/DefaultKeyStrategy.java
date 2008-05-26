package com.googlecode.hibernate.memcached;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class DefaultKeyStrategy implements KeyStrategy {

    public String toKey(String namespace, long namespaceIndex, Object key) {
        return new StringBuilder().append(namespace).append(":").append(namespaceIndex).append(":").append(String.valueOf(key).replace(' ', '_')).toString();
    }

}
