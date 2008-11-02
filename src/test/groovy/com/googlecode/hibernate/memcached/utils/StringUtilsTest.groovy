package com.googlecode.hibernate.memcached.utils

import com.googlecode.hibernate.memcached.BaseTestCase

class StringUtilsTest extends BaseTestCase {

    void test_simple_join() {
        assertEquals "1, 2, 3", StringUtils.join([1, 2, 3] as Object[], ", ")
    }

    void test_empty_join() {
        assertEquals "", StringUtils.join([] as Object[], ", ")
    }

    void test_null_join () {
        assertNull StringUtils.join(null, ",")
    }

    void test_md5_hex () {
        assertEquals "eae4b23daa656ea031c2b90106304cf2", StringUtils.md5Hex("boosh! and/or kakow")
    }

    void test_null_md5_hex () {
        shouldFail(IllegalArgumentException) {
            StringUtils.md5Hex(null)
        }
    }


}