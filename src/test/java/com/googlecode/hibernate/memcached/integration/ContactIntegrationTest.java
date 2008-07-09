package com.googlecode.hibernate.memcached.integration;

import org.hibernate.Criteria;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class ContactIntegrationTest extends AbstractHibernateTestCase {

    Contact ray;

    protected void setupInTransaction() {

        ray = new Contact();
        ray.setFirstName("Ray");
        ray.setLastName("Krueger");
        ray.setBirthday(new Date());
        session.saveOrUpdate(ray);
        session.flush();
        session.clear();
    }

    protected Properties getConfigProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.cache.use_query_cache", "true");
        return props;
    }

    public void test() {
        Contact fromDB = (Contact) session.get(Contact.class, ray.getId());
        assertNotNull(fromDB);
    }

    public void test_query_cache() {
        Criteria criteria = session.createCriteria(Contact.class)
                .add(eq("firstName", "Ray"))
                .add(eq("lastName", "Krueger"))
                .setCacheable(true)
                .setCacheRegion("contact.findByFirstNameAndLastName");

        assertNotNull(criteria.uniqueResult());

        criteria.uniqueResult();
        criteria.uniqueResult();
        criteria.uniqueResult();
        criteria.uniqueResult();

        assertEquals(criteria.uniqueResult(), criteria.uniqueResult());
    }

    public void test_query_cache_with_date() throws Exception {

        Thread.sleep(3000);
        Calendar birthday = Calendar.getInstance();
        birthday.set(Calendar.HOUR_OF_DAY, 0);
        birthday.set(Calendar.MINUTE, 0);
        birthday.set(Calendar.SECOND, 0);
        birthday.set(Calendar.MILLISECOND, 0);

        Criteria criteria = session.createCriteria(Contact.class)
                .add(eq("firstName", "Ray"))
                .add(eq("lastName", "Krueger"))
                .add(eq("birthday", birthday.getTime()))
                .setCacheable(true)
                .setCacheRegion("contact.findByFirstNameAndLastNameAndBirthday");

        assertNotNull(criteria.uniqueResult());
        criteria.uniqueResult();
        criteria.uniqueResult();
        criteria.uniqueResult();

        assertEquals(criteria.uniqueResult(), criteria.uniqueResult());
    }
}
