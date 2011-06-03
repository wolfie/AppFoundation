package org.vaadin.appfoundation.test.authorization.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.vaadin.appfoundation.authorization.jpa.PermissionEntity;
import org.vaadin.appfoundation.authorization.jpa.PermissionType;

public class PermissionEntityTest {

    @Test
    public void constructor() {
        PermissionEntity p = new PermissionEntity(PermissionType.ALLOW);
        assertEquals(PermissionType.ALLOW, p.getType());

        p = new PermissionEntity();
        assertNull(p.getType());
    }

    @Test
    public void type() {
        PermissionEntity p = new PermissionEntity();
        p.setType(PermissionType.ALLOW);

        assertEquals(PermissionType.ALLOW, p.getType());
        p.setType(PermissionType.ALLOW_ALL);

        assertEquals(PermissionType.ALLOW_ALL, p.getType());
    }

    @Test
    public void role() {
        PermissionEntity p = new PermissionEntity();
        p.setRole("foo");

        assertEquals("foo", p.getRole());
        p.setRole("bar");

        assertEquals("bar", p.getRole());
    }

    @Test
    public void action() {
        PermissionEntity p = new PermissionEntity();
        p.setAction("foo");

        assertEquals("foo", p.getAction());
        p.setAction("bar");

        assertEquals("bar", p.getAction());
    }

    @Test
    public void resource() {
        PermissionEntity p = new PermissionEntity();
        p.setResource("foo");

        assertEquals("foo", p.getResource());
        p.setResource("bar");

        assertEquals("bar", p.getResource());
    }

}
