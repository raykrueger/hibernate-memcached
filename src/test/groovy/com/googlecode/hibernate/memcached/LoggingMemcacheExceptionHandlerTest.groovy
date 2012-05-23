package com.googlecode.hibernate.memcached

import org.apache.log4j.Logger

/**
 * This test is lame, I have no idea what I should do to make it better.
 * @author Ray Krueger
 */
class LoggingMemcacheExceptionHandlerTest extends BaseTestCase {

    def handler = new LoggingMemcacheExceptionHandler()
    Logger logger = Logger.getLogger(LoggingMemcacheExceptionHandler)

    protected void setUp() {
        super.setUp()
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
