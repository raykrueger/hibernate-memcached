package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * @author Ray Krueger
 */
public class Sha1KeyStrategy extends DigestKeyStrategy {
  @Override
  protected String digest(String key) {
    return StringUtils.sha1Hex(key);
  }
}
