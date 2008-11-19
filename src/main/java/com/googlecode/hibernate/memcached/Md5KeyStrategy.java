package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * @author Ray Krueger
 */
public class Md5KeyStrategy extends HashCodeKeyStrategy {
    protected String concatenateKey(String regionName, long clearIndex, Object key) {
        String longKey = super.concatenateKey(regionName, clearIndex, key);
        return StringUtils.md5Hex(longKey);
    }
}
