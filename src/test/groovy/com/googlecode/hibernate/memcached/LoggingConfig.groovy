package com.googlecode.hibernate.memcached

import org.apache.log4j.BasicConfigurator

/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
class LoggingConfig {

    static {
        System.setProperty "net.spy.log.LoggerImpl", "net.spy.log.Log4JLogger"
    }

    public static void initializeLogging() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }

}