package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * @author Ray Krueger
 */
public abstract class DigestKeyStrategy extends AbstractKeyStrategy {

  protected String transformKeyObject(Object key) {
    return key.toString() + ":" + key.hashCode();
  }

  protected String concatenateKey(String regionName, long clearIndex, Object key) {
    String longKey = super.concatenateKey(regionName, clearIndex, key);
    return digest(longKey);
  }

  protected abstract String digest(String string);
}
