package com.googlecode.hibernate.memcached
/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
abstract class BaseTestCase extends groovy.util.GroovyTestCase {

    static {
        LoggingConfig.initializeLogging()
    }
}