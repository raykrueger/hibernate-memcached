package com.googlecode.hibernate.memcached;

import java.util.UUID;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class UUIDKeyStrategy extends DefaultKeyStrategy {

    public String toKey(String namespace, long namespaceIndex, Object key) {
        return UUID.nameUUIDFromBytes(
                super.toKey(namespace, namespaceIndex, key).getBytes()
        ).toString();
    }
}
