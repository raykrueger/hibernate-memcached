package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * @author Ray Krueger
 */
public class Md5KeyStrategy extends DigestKeyStrategy {
  @Override
  protected String digest(String key) {
    return StringUtils.md5Hex(key);
  }
}
