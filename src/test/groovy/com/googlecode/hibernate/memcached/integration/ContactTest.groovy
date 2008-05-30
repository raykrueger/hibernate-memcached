package com.googlecode.hibernate.memcached.integration

import static org.hibernate.criterion.Restrictions.*

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class ContactTest extends AbstractHibernateTestCase {

    Contact ray

    protected void setupInTransaction() {
        ray = new Contact()
        ray.setFirstName("Ray")
        ray.setLastName("Krueger")
        session.saveOrUpdate(ray)
        session.flush()
        session.clear()
    }

    protected Properties getConfigProperties() {
        Properties props = new Properties()
        props.setProperty("hibernate.cache.use_query_cache", "true")
        //props.setProperty("hibernate.memcached.keyStrategy", UUIDKeyStrategy.class.getName())
        return props
    }

    void test() {
        Contact fromDB = (Contact) session.get(Contact.class, ray.getId())
        assertNotNull(fromDB)
    }

    void test_query_cache() {
        Contact fromDB = (Contact) session.createCriteria(Contact.class).add(eq("firstName", "Ray")).add(eq("lastName", "Krueger")).setCacheable(true).setCacheRegion("contact.findByFirstNameAndLastName").uniqueResult()
        assertEquals(ray, fromDB)
    }

}
