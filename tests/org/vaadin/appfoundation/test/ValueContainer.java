package org.vaadin.appfoundation.test;

public class ValueContainer {

    private Object value;

    public ValueContainer() {
    }

    public ValueContainer(Object value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
