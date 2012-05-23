package com.googlecode.hibernate.memcached

import java.util.concurrent.TimeUnit

class PropertiesHelperTest extends BaseTestCase {

    PropertiesHelper helper

    protected void setUp() {
        super.setUp()
        helper = newHelper()
    }

    void test_strings() {
        assertEquals "world", helper.get("hello")
        assertEquals "world", helper.get("hello", "blah")
        assertEquals "default", helper.get("nothing", "default")
    }

    void test_boolean() {
        assertFalse helper.getBoolean("blah", false)
        assertTrue helper.getBoolean("blah", true)
        assertTrue helper.getBoolean("thisIsTrue", false)
        assertFalse helper.getBoolean("thisIsFalse", true)

        //Boolean.parseBoolean returns false when it can't parse the value
        assertFalse helper.getBoolean("hello", true)
    }

    void test_long() {
        assertEquals 1L, helper.getLong("one", 10)
        assertEquals 10L, helper.getLong("nothing", 10)

        shouldFail(NumberFormatException) {
            helper.getLong("hello", 10)
        }
    }

    void test_int() {
        assertEquals 1, helper.getInt("one", 10)
        assertEquals 10, helper.getInt("nothing", 10)

        shouldFail(NumberFormatException) {
            helper.getInt("hello", 10)
        }
    }

    void test_enum() {
        assertEquals TimeUnit.SECONDS, helper.getEnum("seconds", TimeUnit, TimeUnit.NANOSECONDS)
        assertEquals TimeUnit.NANOSECONDS, helper.getEnum("nothing", TimeUnit, TimeUnit.NANOSECONDS)

        shouldFail(IllegalArgumentException) {
            helper.getEnum("hello", TimeUnit, TimeUnit.NANOSECONDS)
        }
    }

    void test_find_values() {
        assertNull helper.findValue("this", "does", "not", "exist")
        assertEquals "world", helper.findValue("this", "does", "not", "exist", "hello")
    }

    PropertiesHelper newHelper() {
        new PropertiesHelper(
            [hello: "world",
             one: "1",
             thisIsTrue: "true",
             thisIsFalse: "false",
             seconds: "SECONDS"] as Properties)
    }
}
