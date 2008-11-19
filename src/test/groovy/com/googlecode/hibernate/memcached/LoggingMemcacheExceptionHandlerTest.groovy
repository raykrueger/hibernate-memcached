package com.googlecode.hibernate.memcached

import org.apache.log4j.Appender
import org.apache.log4j.Layout
import org.apache.log4j.Logger
import org.apache.log4j.spi.ErrorHandler
import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent
import org.junit.Assert

/**
 * This test is lame, I have no idea what I should do to make it better.
 * @author Ray Krueger
 */
class LoggingMemcacheExceptionHandlerTest extends BaseTestCase {

    def handler = new LoggingMemcacheExceptionHandler()
    Logger logger = Logger.getLogger(LoggingMemcacheExceptionHandler)

    protected void setUp() {
        logger.removeAllAppenders()
    }

    void testDelete() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'delete' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnDelete "blah", exception
        assert appender.appenderCalled
    }

    void testGet() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'get' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnGet "blah", exception
        assert appender.appenderCalled
    }

    void testIncr() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'incr' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnIncr "blah", 10, 20, exception
        assert appender.appenderCalled
    }

    void testSet() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'set' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnSet "blah", 300, new Object(), exception
        assert appender.appenderCalled
    }

}
