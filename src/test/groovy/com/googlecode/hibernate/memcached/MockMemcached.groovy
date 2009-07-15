package com.googlecode.hibernate.memcached
/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
class MockMemcached implements Memcache {

    def cache = [:]

    public Object get(String key) {
        cache[key]
    }

    public void set(String key, int cacheTimeSeconds, Object o) {
        cache[key] = o
    }

    public void delete(String key) {
        cache.remove key
    }

    public void incr(String key, int factor, int startingValue) {
        Integer counter = (Integer) cache[key]
        if (counter != null) {
            cache[key] = counter + 1
        } else {
            cache[key] = counter
        }
    }

    public void shutdown() {

    }


  public Map<String, Object> getMulti(String[] keys) {
    return cache.findAll {key, value -> keys.toList().contains(key)}
  }
}