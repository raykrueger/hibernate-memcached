# Hibernate-memcached
A library for using Memcached as a second level distributed cache in Hibernate.

  * Based on the excellent spymemcached client
  * Includes support for the Whalin (danga) memcached client
  * Supports entity and query caching.

# Help
If you have any questions, or just want to drop a line to say it's working great :) use the [google-group](http://groups.google.com/group/hibernate-memcached).

Please note that this is an open source project. I work on it when I can and I implement what I feel like. I am volunteering my own free time for my own amusement.

# Versions
## 1.3-SNAPSHOT
  * [HashCodeKeyStrategy][1] [StringKeyStrategy][2] are now both deprecated.
  * [Sha1KeyStrategy][3] is now the default strategy.
  * [Md5KeyStrategy][4] and [Sha1KeyStrategy][3] both digest the entire combined key now (region, clear index, key)
  * [Md5KeyStrategy][4] and [Sha1KeyStrategy][3] have been re-implemented to hash both the toString() and hashCode() values
    of the Hibernate query key object. This should eliminate collisions when using hashCode() alone. 
    [Issue 22](http://code.google.com/p/hibernate-memcached/issues/detail?id=22).
  * [HashCodeKeyStrategy][1] [StringKeyStrategy][2] will throw exceptions now if the key length is greater than 250.

As a result of these changes hibernate-memcached will miss on all cache requests upon upgrading to this version. This
is due to the switch to Sha1KeyStrategy as the default. Hibernate-memcached will now generate different keys for the
same data you were caching previously. Essentially, your cache will appear empty to Hibernate.

Also, as a result of these changes, 1.3 may not be binary compatible with any subclass hacks you may have written that
extend HashCodeKeyStrategy, StringKeyStrategy, Md5KeyStrategy, or Sha1KeyStrategy. Note that the KeyStrategy interface 
and AbstractKeyStrategy have not changed at all. If you implemented/extended those directly you're fine.

## 1.2.2
  * Patch from ddlatham to allow the spymemcached library to be put 
    into daemon mode. This is accomplished by setting 
    hibernate.memcached.daemonMode to true. 
  * Updated the maven pom to pull in spymemcached 2.4.2 by default. 

# Note on Patches/Pull Requests

  * Fork the project.
  * Make your feature addition or bug fix.
  * Add tests for it. This is important so I don't break it in a future version unintentionally.
  * Commit, do not mess with pom.xml, version, or history. (if you want to have your own version, that is fine but bump version in a commit by itself I can ignore when I pull)
  * Send me a pull request. Bonus points for topic branches.

[1]: hibernate-memcached/blob/master/src/main/java/com/googlecode/hibernate/memcached/HashCodeKeyStrategy.java
[2]: hibernate-memcached/blob/master/src/main/java/com/googlecode/hibernate/memcached/StringKeyStrategy.java
[3]: hibernate-memcached/blob/master/src/main/java/com/googlecode/hibernate/memcached/Sha1KeyStrategy.java
[4]: hibernate-memcached/blob/master/src/main/java/com/googlecode/hibernate/memcached/Md5KeyStrategy.java
