package com.googlecode.hibernate.memcached;

import junit.framework.Assert;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Ray Krueger
 */
class MockAppender implements Appender {

    String expectedMessage;
    Exception expectedError;
    boolean appenderCalled = false;

    MockAppender(String expectedMessage, Exception expectedError) {
        this.expectedMessage = expectedMessage;
        this.expectedError = expectedError;
    }

    public void addFilter(Filter newFilter) {
        throw new UnsupportedOperationException();
    }

    public Filter getFilter() {
        throw new UnsupportedOperationException();
    }

    public void clearFilters() {
        throw new UnsupportedOperationException();
    }

    public void close() {
    }

    public void doAppend(LoggingEvent event) {
        Assert.assertEquals(expectedMessage, event.getMessage());
        Assert.assertEquals(expectedError, event.getThrowableInformation().getThrowable());
        appenderCalled = true;
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        throw new UnsupportedOperationException();
    }

    public ErrorHandler getErrorHandler() {
        throw new UnsupportedOperationException();
    }

    public void setLayout(Layout layout) {
        throw new UnsupportedOperationException();
    }

    public Layout getLayout() {
        throw new UnsupportedOperationException();
    }

    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public boolean requiresLayout() {
        throw new UnsupportedOperationException();
    }
}