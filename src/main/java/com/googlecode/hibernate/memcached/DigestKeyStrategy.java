package com.googlecode.hibernate.memcached;

/**
 * @author Ray Krueger
 */
public abstract class DigestKeyStrategy extends AbstractKeyStrategy {

  @Override
  protected String transformKeyObject(Object key) {
    return key.toString() + ":" + key.hashCode();
  }

  @Override
  protected String concatenateKey(String regionName, long clearIndex, Object key) {
    String longKey = super.concatenateKey(regionName, clearIndex, key);
    return digest(longKey);
  }

  protected abstract String digest(String string);
}
