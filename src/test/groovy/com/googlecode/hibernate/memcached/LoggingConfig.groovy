package com.googlecode.hibernate.memcached

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger


/**
* DOCUMENT ME!
* @author Ray Krueger
*/
class LoggingConfig {

    static {
        System.setProperty "net.spy.log.LoggerImpl", "net.spy.log.Log4JLogger"
    }

    public static void initializeLogging() {
        initializeLogging("INFO")
    }

    public static void initializeLogging(String level) {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.toLevel(level))
    }

}