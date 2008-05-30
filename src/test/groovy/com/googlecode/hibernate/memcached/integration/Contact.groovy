package com.googlecode.hibernate.memcached.integration

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy


/**
 * @author Ray Krueger
 */
@Entity
@Cache (region = "contact_cache", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Contact {

    @Id
    @GeneratedValue
    Long id

    String firstName

    String lastName

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    String getFirstName() {
        return firstName
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    String getLastName() {
        return lastName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
    }

    boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false

        Contact contact = (Contact) o

        if (id != null ? !id.equals(contact.id) : contact.id != null) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }
}
