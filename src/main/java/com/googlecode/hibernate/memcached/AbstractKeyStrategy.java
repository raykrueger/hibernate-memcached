package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * KeyStrategy base class that handles concatenation, cleaning, and truncating the final cache key.
 * <p/>
 * Concatenates the three key components; regionName, clearIndex and key.<br/>
 * Subclasses are responsible for transforming the Key object into something identifyable.<br/>
 * If the key total length, including region and clearIndex, are greater than the maxKeyLength, the key's hashCode
 * will be used as the key. Subclasses can override this behavior.
 *
 * @author Ray Krueger
 */
public abstract class AbstractKeyStrategy implements KeyStrategy {

    public static final int DEFAULT_MAX_KEY_LENGTH = 250;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final Pattern CLEAN_PATTERN = Pattern.compile("\\s");

    private int maxKeyLength = DEFAULT_MAX_KEY_LENGTH;

    public String toKey(String regionName, long clearIndex, Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        String keyString = concatenateKey(regionName, clearIndex, transformKeyObject(key));

        if (keyString.length() > maxKeyLength) {
            return truncateKey(keyString);
        }

        String finalKey = CLEAN_PATTERN.matcher(keyString).replaceAll("");
        log.debug("Final cache key: [{}]", finalKey);
        return finalKey;
    }

    protected abstract String transformKeyObject(Object key);

    protected String truncateKey(String key) {

        String keyHashCode = StringUtils.md5Hex(key);

        log.warn("Encoded key [{}] to md5 hash [{}]. " +
                "Be sure to set cache region names whenever possible as the names Hibernate generates are really long.",
                key, keyHashCode
        );

        return keyHashCode;
    }

    public int getMaxKeyLength() {
        return maxKeyLength;
    }

    public void setMaxKeyLength(int maxKeyLength) {
        this.maxKeyLength = maxKeyLength;
    }

    protected String concatenateKey(String regionName, long clearIndex, Object key) {
        return new StringBuilder()
                .append(regionName)
                .append(":")
                .append(clearIndex)
                .append(":")
                .append(String.valueOf(key)).toString();
    }
}
