package org.vaadin.appfoundation.test.persistence;

import javax.persistence.Entity;

import org.vaadin.appfoundation.persistence.data.AbstractPojo;

@Entity
public class MockPojo extends AbstractPojo {

    private static final long serialVersionUID = -843467556446902061L;

    private String foo;

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

}
