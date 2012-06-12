package com.googlecode.hibernate.memcached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * KeyStrategy base class that handles concatenation, cleaning, and truncating the final cache key.
 * <p/>
 * Concatenates the three key components; regionName, clearIndex and key.<br/>
 * Subclasses are responsible for transforming the Key object into something identifyable.
 *
 * @author Ray Krueger
 */
public abstract class AbstractKeyStrategy implements KeyStrategy {

    public static final int MAX_KEY_LENGTH = 250;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final Pattern CLEAN_PATTERN = Pattern.compile("\\s");

    public String toKey(String regionName, long clearIndex, Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        String keyString = concatenateKey(regionName, clearIndex, transformKeyObject(key));

        if (keyString.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException("Key is longer than " + MAX_KEY_LENGTH + " characters, try using the Sha1KeyStrategy: " + keyString);
        }

        String finalKey = CLEAN_PATTERN.matcher(keyString).replaceAll("");
        log.debug("Final cache key: [{}]", finalKey);
        return finalKey;
    }

    protected abstract String transformKeyObject(Object key);

    protected String concatenateKey(String regionName, long clearIndex, Object key) {
        return new StringBuilder()
                .append(regionName)
                .append(":")
                .append(clearIndex)
                .append(":")
                .append(String.valueOf(key)).toString();
    }
}
