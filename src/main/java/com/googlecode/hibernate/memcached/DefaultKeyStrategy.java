package com.googlecode.hibernate.memcached;

import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class DefaultKeyStrategy implements KeyStrategy {

    private static final Pattern CLEAN_PATTERN = Pattern.compile("\\s");

    public String toKey(String namespace, long namespaceIndex, Object key) {

        StringBuilder stringKey = new StringBuilder()
                .append(namespace)
                .append(":")
                .append(namespaceIndex)
                .append(":")
                .append(String.valueOf(key));

        return CLEAN_PATTERN.matcher(stringKey).replaceAll("");
    }
}
