package com.googlecode.hibernate.memcached;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public abstract class BaseTestCase extends TestCase {

    static {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }


}
