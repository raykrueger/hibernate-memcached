package com.googlecode.hibernate.memcached

/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
class MockMemcached implements Memcache {

    def cache = [:]

    Object get(String key) {
        cache[key]
    }

    void set(String key, int cacheTimeSeconds, Object o) {
        cache[key] = o
    }

    void delete(String key) {
        cache.remove key
    }

    void incr(String key, int factor, int startingValue) {
        Integer counter = cache[key]
        if (counter == null) {
            cache[key] = counter
        } else {
            cache[key] = counter + 1
        }
    }

    void shutdown() {

    }

    Map<String, Object> getMulti(String[] keys) {
        cache.findAll {key, value -> keys.toList().contains(key)}
    }
}
