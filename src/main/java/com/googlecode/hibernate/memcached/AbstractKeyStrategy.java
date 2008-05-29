package com.googlecode.hibernate.memcached;

/**
 * This class simply uses a {@link StringBuilder} to append the regionName, clearIndex, and Key together.
 * This class makes no attempt to truncate or clean the key. Meant to be used as a convenience to subclasses.
 *
 * @author Ray Krueger
 */
public abstract class AbstractKeyStrategy implements KeyStrategy {

    protected String convertKeyToString(String regionName, long clearIndex, Object key) {
        return new StringBuilder()
                .append(regionName)
                .append(":")
                .append(clearIndex)
                .append(":")
                .append(String.valueOf(key)).toString();
    }
}
